package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Address;
import com.mystore.persistence.AddressMapper;
import com.mystore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public CommonResponse<Address> addAddress(Address address,Integer userId){
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        int result=addressMapper.insert(address);
        if(result!=1){
            return CommonResponse.createForError("新建地址失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<Object> deleteAddress(Integer addressId,Integer userId){
        //涉及横向越权问题，不能只传入addressId，否则会造成一个用户可以删除别人的地址
        QueryWrapper<Address> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("id",addressId);

        int result=addressMapper.delete(queryWrapper);
        if(result!=1){
            return CommonResponse.createForError("删除地址失败");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Address> updateAddress(Integer userId,Address address){
        address.setUserId(userId);
        address.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Address> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("user_id",userId).eq("id",address.getId());
        int result=addressMapper.update(address,updateWrapper);
        if(result!=1){
            return CommonResponse.createForError("修改地址失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<Address> findById(Integer userId,Integer addressId){
        QueryWrapper<Address> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("id",addressId);

        Address address=addressMapper.selectOne(queryWrapper);
        if(address==null){
            return CommonResponse.createForError("获取地址信息失败");
        }
        return CommonResponse.createForSuccess(address);
    }

    @Override
    public CommonResponse<List<Address>> findAll(Integer userId){
        QueryWrapper<Address> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Address> addressList= addressMapper.selectList(queryWrapper);
        if (addressList.isEmpty()){
            return CommonResponse.createForError("查询地址信息失败");
        }
        return CommonResponse.createForSuccess(addressList);
    }
}
