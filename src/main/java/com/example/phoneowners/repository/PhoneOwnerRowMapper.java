package com.example.phoneowners.repository;

import com.example.phoneowners.entity.Address;
import com.example.phoneowners.entity.PhoneOwner;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhoneOwnerRowMapper implements RowMapper<PhoneOwner> {

    @Override
    public PhoneOwner mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Address address = new Address();
        address.setId(resultSet.getLong("address_id"));
        address.setPostalCode(resultSet.getString("postal_code"));
        address.setCountry(resultSet.getString("country"));
        address.setRegion(resultSet.getString("region"));
        address.setDistrict(resultSet.getString("district"));
        address.setCity(resultSet.getString("city"));
        address.setStreet(resultSet.getString("street"));
        address.setHouse(resultSet.getString("house"));
        address.setApartment(resultSet.getString("apartment"));

        PhoneOwner owner = new PhoneOwner();
        owner.setId(resultSet.getLong("owner_id"));
        owner.setLastName(resultSet.getString("last_name"));
        owner.setFirstName(resultSet.getString("first_name"));
        owner.setMiddleName(resultSet.getString("middle_name"));
        owner.setPhoneNumber(resultSet.getString("phone_number"));
        owner.setAddress(address);
        return owner;
    }
}
