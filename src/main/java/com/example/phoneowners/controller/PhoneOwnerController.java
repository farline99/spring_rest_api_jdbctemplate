package com.example.phoneowners.controller;

import com.example.phoneowners.dto.PhoneNumberReassignmentRequest;
import com.example.phoneowners.dto.PhoneNumberReassignmentResponse;
import com.example.phoneowners.dto.PhoneNumberUpdateRequest;
import com.example.phoneowners.dto.PhoneOwnerPatchRequest;
import com.example.phoneowners.dto.PhoneOwnerRequest;
import com.example.phoneowners.dto.PhoneOwnerResponse;
import com.example.phoneowners.dto.PhoneOwnerStatistics;
import com.example.phoneowners.service.PhoneOwnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class PhoneOwnerController {

    private final PhoneOwnerService ownerService;

    public PhoneOwnerController(PhoneOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public List<PhoneOwnerResponse> findAll() {
        return ownerService.findAll();
    }

    @GetMapping("/{id}")
    public PhoneOwnerResponse findById(@PathVariable Long id) {
        return ownerService.findById(id);
    }

    @GetMapping("/statistics")
    public PhoneOwnerStatistics getStatistics(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city) {
        return ownerService.getStatistics(country, city);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhoneOwnerResponse create(@Valid @RequestBody PhoneOwnerRequest request) {
        return ownerService.create(request);
    }

    @PutMapping("/{id}")
    public PhoneOwnerResponse update(@PathVariable Long id,
                                     @Valid @RequestBody PhoneOwnerRequest request) {
        return ownerService.update(id, request);
    }

    @PatchMapping("/{id}")
    public PhoneOwnerResponse patch(@PathVariable Long id,
                                    @Valid @RequestBody PhoneOwnerPatchRequest request) {
        return ownerService.patch(id, request);
    }

    @PutMapping("/{id}/phone-number")
    public PhoneOwnerResponse updatePhoneNumber(
            @PathVariable Long id,
            @Valid @RequestBody PhoneNumberUpdateRequest request) {
        return ownerService.updatePhoneNumber(id, request);
    }

    @PutMapping("/reassign")
    public PhoneNumberReassignmentResponse reassignPhoneNumber(
            @Valid @RequestBody PhoneNumberReassignmentRequest request) {
        return ownerService.reassignPhoneNumber(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ownerService.delete(id);
    }
}
