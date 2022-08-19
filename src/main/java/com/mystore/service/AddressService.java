package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.domain.Address;
import com.mystore.vo.AddressVO;

import java.util.List;

public interface AddressService {
    CommonResponse<AddressVO> addAddress(Address address, Integer userId);
    CommonResponse<Object> deleteAddress(Integer addressId,Integer userId);
    CommonResponse<AddressVO> updateAddress(Integer userId,Address address);
    CommonResponse<AddressVO> findById(Integer userId,Integer addressId);
    CommonResponse<List<AddressVO>> findAll(Integer userId);
}
