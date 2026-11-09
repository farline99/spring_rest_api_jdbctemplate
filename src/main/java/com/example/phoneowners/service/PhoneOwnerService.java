package com.example.phoneowners.service;

import com.example.phoneowners.dto.AddressPatchRequest;
import com.example.phoneowners.dto.AddressRequest;
import com.example.phoneowners.dto.AddressResponse;
import com.example.phoneowners.dto.PhoneNumberReassignmentRequest;
import com.example.phoneowners.dto.PhoneNumberReassignmentResponse;
import com.example.phoneowners.dto.PhoneNumberUpdateRequest;
import com.example.phoneowners.dto.PhoneOwnerPatchRequest;
import com.example.phoneowners.dto.PhoneOwnerRequest;
import com.example.phoneowners.dto.PhoneOwnerResponse;
import com.example.phoneowners.dto.PhoneOwnerStatistics;
import com.example.phoneowners.entity.Address;
import com.example.phoneowners.entity.PhoneOwner;
import com.example.phoneowners.exception.BadRequestException;
import com.example.phoneowners.exception.ConflictException;
import com.example.phoneowners.exception.ResourceNotFoundException;
import com.example.phoneowners.repository.PhoneOwnerRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class PhoneOwnerService {

    private final PhoneOwnerRepository ownerRepository;
    private final Validator validator;

    public PhoneOwnerService(PhoneOwnerRepository ownerRepository, Validator validator) {
        this.ownerRepository = ownerRepository;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public List<PhoneOwnerResponse> findAll() {
        return ownerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PhoneOwnerResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public PhoneOwnerResponse create(PhoneOwnerRequest request) {
        validate(request);
        String phoneNumber = request.phoneNumber().trim();
        if (ownerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("Владелец с номером телефона " + phoneNumber + " уже существует");
        }

        PhoneOwner owner = new PhoneOwner();
        Address address = new Address();
        applyOwner(owner, request.lastName(), request.firstName(), request.middleName(), phoneNumber);
        applyAddress(address, request.address());
        owner.setAddress(address);
        return toResponse(ownerRepository.save(owner));
    }

    @Transactional
    public PhoneOwnerResponse update(Long id, PhoneOwnerRequest request) {
        validate(request);
        PhoneOwner owner = findEntity(id);
        String phoneNumber = request.phoneNumber().trim();
        if (ownerRepository.existsByPhoneNumberAndIdNot(phoneNumber, id)) {
            throw new ConflictException("Владелец с номером телефона " + phoneNumber + " уже существует");
        }

        applyOwner(owner, request.lastName(), request.firstName(), request.middleName(), phoneNumber);
        applyAddress(owner.getAddress(), request.address());
        return toResponse(ownerRepository.save(owner));
    }

    @Transactional
    public PhoneOwnerResponse patch(Long id, PhoneOwnerPatchRequest request) {
        PhoneOwner owner = findEntity(id);
        Address address = owner.getAddress();
        AddressPatchRequest addressPatch = request.address();

        AddressRequest mergedAddress = new AddressRequest(
                valueOrExisting(addressPatch == null ? null : addressPatch.postalCode(), address.getPostalCode()),
                valueOrExisting(addressPatch == null ? null : addressPatch.country(), address.getCountry()),
                valueOrExisting(addressPatch == null ? null : addressPatch.region(), address.getRegion()),
                valueOrExisting(addressPatch == null ? null : addressPatch.district(), address.getDistrict()),
                valueOrExisting(addressPatch == null ? null : addressPatch.city(), address.getCity()),
                valueOrExisting(addressPatch == null ? null : addressPatch.street(), address.getStreet()),
                valueOrExisting(addressPatch == null ? null : addressPatch.house(), address.getHouse()),
                valueOrExisting(addressPatch == null ? null : addressPatch.apartment(), address.getApartment())
        );
        PhoneOwnerRequest mergedRequest = new PhoneOwnerRequest(
                valueOrExisting(request.lastName(), owner.getLastName()),
                valueOrExisting(request.firstName(), owner.getFirstName()),
                valueOrExisting(request.middleName(), owner.getMiddleName()),
                mergedAddress,
                valueOrExisting(request.phoneNumber(), owner.getPhoneNumber())
        );
        validate(mergedRequest);

        String phoneNumber = mergedRequest.phoneNumber().trim();
        if (ownerRepository.existsByPhoneNumberAndIdNot(phoneNumber, id)) {
            throw new ConflictException("Владелец с номером телефона " + phoneNumber + " уже существует");
        }

        applyOwner(owner, mergedRequest.lastName(), mergedRequest.firstName(),
                mergedRequest.middleName(), phoneNumber);
        applyAddress(address, mergedAddress);
        return toResponse(ownerRepository.save(owner));
    }

    @Transactional
    public PhoneOwnerResponse updatePhoneNumber(Long id, PhoneNumberUpdateRequest request) {
        PhoneOwner owner = findEntity(id);
        String phoneNumber = request.phoneNumber().trim();
        if (ownerRepository.existsByPhoneNumberAndIdNot(phoneNumber, id)) {
            throw new ConflictException("Владелец с номером телефона " + phoneNumber + " уже существует");
        }
        ownerRepository.updatePhoneNumber(id, phoneNumber);
        owner.setPhoneNumber(phoneNumber);
        return toResponse(owner);
    }

    @Transactional
    public PhoneNumberReassignmentResponse reassignPhoneNumber(PhoneNumberReassignmentRequest request) {
        String requestedPhone = request.phoneNumber().trim();
        PhoneOwner source = ownerRepository.findByPhoneNumber(requestedPhone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Владелец с номером телефона " + requestedPhone + " не найден"));
        PhoneOwner target = findEntity(request.targetOwnerId());
        if (source.getId().equals(target.getId())) {
            throw new BadRequestException("Нельзя обменять номер телефона владельца с самим собой");
        }

        String sourcePhone = source.getPhoneNumber();
        String targetPhone = target.getPhoneNumber();
        ownerRepository.updatePhoneNumber(source.getId(), "TMP-" + UUID.randomUUID().toString().substring(0, 24));
        ownerRepository.updatePhoneNumber(target.getId(), sourcePhone);
        ownerRepository.updatePhoneNumber(source.getId(), targetPhone);
        source.setPhoneNumber(targetPhone);
        target.setPhoneNumber(sourcePhone);

        return new PhoneNumberReassignmentResponse(toResponse(source), toResponse(target));
    }

    @Transactional
    public void delete(Long id) {
        findEntity(id);
        ownerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PhoneOwnerStatistics getStatistics(String country, String city) {
        List<PhoneOwner> owners = ownerRepository.findAll().stream()
                .filter(owner -> matches(owner.getAddress().getCountry(), country))
                .filter(owner -> matches(owner.getAddress().getCity(), city))
                .toList();

        Map<String, Long> byCountry = counts(owners, true);
        Map<String, Long> byCity = counts(owners, false);
        return new PhoneOwnerStatistics(
                owners.size(),
                byCountry.size(),
                byCity.size(),
                mostCommon(byCity),
                mostCommonStreet(owners),
                byCountry,
                byCity
        );
    }

    private PhoneOwner findEntity(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Владелец с ID " + id + " не найден"));
    }

    private void validate(PhoneOwnerRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void applyOwner(PhoneOwner owner, String lastName, String firstName, String middleName,
                            String phoneNumber) {
        owner.setLastName(lastName.trim());
        owner.setFirstName(firstName.trim());
        owner.setMiddleName(middleName.trim());
        owner.setPhoneNumber(phoneNumber);
    }

    private void applyAddress(Address address, AddressRequest request) {
        address.setPostalCode(request.postalCode().trim());
        address.setCountry(request.country().trim());
        address.setRegion(request.region().trim());
        address.setDistrict(request.district().trim());
        address.setCity(request.city().trim());
        address.setStreet(request.street().trim());
        address.setHouse(request.house().trim());
        address.setApartment(request.apartment().trim());
    }

    private String valueOrExisting(String value, String existing) {
        return value == null ? existing : value;
    }

    private boolean matches(String value, String filter) {
        return filter == null || filter.isBlank() || value.equalsIgnoreCase(filter.trim());
    }

    private Map<String, Long> counts(List<PhoneOwner> owners, boolean byCountry) {
        Map<String, Long> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (PhoneOwner owner : owners) {
            String value = byCountry ? owner.getAddress().getCountry() : owner.getAddress().getCity();
            result.merge(value, 1L, Long::sum);
        }
        return result;
    }

    private String mostCommon(Map<String, Long> counts) {
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String mostCommonStreet(List<PhoneOwner> owners) {
        Map<String, Long> streets = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (PhoneOwner owner : owners) {
            streets.merge(owner.getAddress().getStreet(), 1L, Long::sum);
        }
        return mostCommon(streets);
    }

    private PhoneOwnerResponse toResponse(PhoneOwner owner) {
        Address address = owner.getAddress();
        AddressResponse addressResponse = new AddressResponse(
                address.getId(),
                address.getPostalCode(),
                address.getCountry(),
                address.getRegion(),
                address.getDistrict(),
                address.getCity(),
                address.getStreet(),
                address.getHouse(),
                address.getApartment()
        );
        return new PhoneOwnerResponse(
                owner.getId(),
                owner.getLastName(),
                owner.getFirstName(),
                owner.getMiddleName(),
                addressResponse,
                owner.getPhoneNumber()
        );
    }
}
