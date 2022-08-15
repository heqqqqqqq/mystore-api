package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.domain.Address;

import java.util.List;

public interface AddressService {
    CommonResponse<Address> addAddress(Address address, Integer userId);
    CommonResponse<Object> deleteAddress(Integer addressId,Integer userId);
    CommonResponse<Address> updateAddress(Integer userId,Address address);
    CommonResponse<Address> findById(Integer userId,Integer addressId);
    CommonResponse<List<Address>> findAll(Integer userId);
}
