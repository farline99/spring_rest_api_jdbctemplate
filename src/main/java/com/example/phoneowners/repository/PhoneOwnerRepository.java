package com.example.phoneowners.repository;

import com.example.phoneowners.entity.Address;
import com.example.phoneowners.entity.PhoneOwner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PhoneOwnerRepository {

    private static final String SELECT_OWNERS = """
            select o.id as owner_id,
                   a.id as address_id,
                   o.last_name,
                   o.first_name,
                   o.middle_name,
                   o.phone_number,
                   a.postal_code,
                   a.country,
                   a.region,
                   a.district,
                   a.city,
                   a.street,
                   a.house,
                   a.apartment
            from phone_owners o
            join addresses a on a.owner_id = o.id
            """;

    private final JdbcTemplate jdbcTemplate;
    private final PhoneOwnerRowMapper rowMapper = new PhoneOwnerRowMapper();

    public PhoneOwnerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PhoneOwner> findAll() {
        return jdbcTemplate.query(SELECT_OWNERS + " order by o.id", rowMapper);
    }

    public Optional<PhoneOwner> findById(Long id) {
        return jdbcTemplate.query(SELECT_OWNERS + " where o.id = ?", rowMapper, id)
                .stream()
                .findFirst();
    }

    public Optional<PhoneOwner> findByPhoneNumber(String phoneNumber) {
        return jdbcTemplate.query(SELECT_OWNERS + " where o.phone_number = ?", rowMapper, phoneNumber)
                .stream()
                .findFirst();
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        Long count = jdbcTemplate.queryForObject(
                "select count(*) from phone_owners where phone_number = ?", Long.class, phoneNumber);
        return count != null && count > 0;
    }

    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id) {
        Long count = jdbcTemplate.queryForObject(
                "select count(*) from phone_owners where phone_number = ? and id <> ?",
                Long.class, phoneNumber, id);
        return count != null && count > 0;
    }

    public PhoneOwner save(PhoneOwner owner) {
        if (owner.getId() == null) {
            insertOwner(owner);
            insertAddress(owner);
        } else {
            updateOwner(owner);
            updateAddress(owner);
        }
        return owner;
    }

    public void updatePhoneNumber(Long id, String phoneNumber) {
        jdbcTemplate.update("update phone_owners set phone_number = ? where id = ?", phoneNumber, id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from addresses where owner_id = ?", id);
        jdbcTemplate.update("delete from phone_owners where id = ?", id);
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from addresses");
        jdbcTemplate.update("delete from phone_owners");
    }

    private void insertOwner(PhoneOwner owner) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into phone_owners (last_name, first_name, middle_name, phone_number) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, owner.getLastName());
            statement.setString(2, owner.getFirstName());
            statement.setString(3, owner.getMiddleName());
            statement.setString(4, owner.getPhoneNumber());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }
        owner.setId(key.longValue());
    }

    private void insertAddress(PhoneOwner owner) {
        Address address = owner.getAddress();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into addresses (postal_code, country, region, district, city, street, house, apartment, owner_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, address.getPostalCode());
            statement.setString(2, address.getCountry());
            statement.setString(3, address.getRegion());
            statement.setString(4, address.getDistrict());
            statement.setString(5, address.getCity());
            statement.setString(6, address.getStreet());
            statement.setString(7, address.getHouse());
            statement.setString(8, address.getApartment());
            statement.setLong(9, owner.getId());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }
        address.setId(key.longValue());
    }

    private void updateOwner(PhoneOwner owner) {
        jdbcTemplate.update("""
                        update phone_owners
                        set last_name = ?, first_name = ?, middle_name = ?, phone_number = ?
                        where id = ?
                        """,
                owner.getLastName(),
                owner.getFirstName(),
                owner.getMiddleName(),
                owner.getPhoneNumber(),
                owner.getId());
    }

    private void updateAddress(PhoneOwner owner) {
        Address address = owner.getAddress();
        jdbcTemplate.update("""
                        update addresses
                        set postal_code = ?, country = ?, region = ?, district = ?, city = ?,
                            street = ?, house = ?, apartment = ?
                        where owner_id = ?
                        """,
                address.getPostalCode(),
                address.getCountry(),
                address.getRegion(),
                address.getDistrict(),
                address.getCity(),
                address.getStreet(),
                address.getHouse(),
                address.getApartment(),
                owner.getId());
    }
}
