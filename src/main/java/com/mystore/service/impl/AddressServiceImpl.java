package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Address;
import com.mystore.persistence.AddressMapper;
import com.mystore.service.AddressService;
import com.mystore.utils.DateTimeFormatUtil;
import com.mystore.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public CommonResponse<AddressVO> addAddress(Address address,Integer userId){
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        int result=addressMapper.insert(address);
        if(result!=1){
            return CommonResponse.createForError("新建地址失败");
        }
        return CommonResponse.createForSuccess(addressToAddressVO(address));
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
    public CommonResponse<AddressVO> updateAddress(Integer userId,Address address){
        address.setUserId(userId);
        address.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Address> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("user_id",userId).eq("id",address.getId());
        int result=addressMapper.update(address,updateWrapper);
        if(result!=1){
            return CommonResponse.createForError("修改地址失败");
        }
        return CommonResponse.createForSuccess(addressToAddressVO(address));
    }

    @Override
    public CommonResponse<AddressVO> findById(Integer userId,Integer addressId){
        QueryWrapper<Address> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("id",addressId);

        Address address=addressMapper.selectOne(queryWrapper);
        if(address==null){
            return CommonResponse.createForError("获取地址信息失败");
        }
        return CommonResponse.createForSuccess(addressToAddressVO(address));
    }

    @Override
    public CommonResponse<List<AddressVO>> findAll(Integer userId){
        QueryWrapper<Address> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Address> addressList= addressMapper.selectList(queryWrapper);
        List<AddressVO> addressVOList= Lists.newArrayList();
        for (Address address:addressList){
            AddressVO addressVO=addressToAddressVO(address);
            addressVOList.add(addressVO);
        }
        if (addressList.isEmpty()){
            return CommonResponse.createForError("查询地址信息失败");
        }
        return CommonResponse.createForSuccess(addressVOList);
    }

    private AddressVO addressToAddressVO(Address address){
        AddressVO addressVO=new AddressVO();

        addressVO.setId(address.getId());
        addressVO.setUserId(address.getUserId());
        addressVO.setAddressName(address.getAddressName());
        addressVO.setAddressPhone(address.getAddressPhone());
        addressVO.setAddressMobile(address.getAddressMobile());
        addressVO.setAddressProvince(address.getAddressProvince());
        addressVO.setAddressCity(address.getAddressCity());
        addressVO.setAddressDistrict(address.getAddressDistrict());
        addressVO.setAddressDetail(address.getAddressDetail());
        addressVO.setAddressZip(address.getAddressZip());
        //处理时间的toString
        addressVO.setCreateTime(DateTimeFormatUtil.format(address.getCreateTime()));
        addressVO.setUpdateTime(DateTimeFormatUtil.format(address.getUpdateTime()));

        return addressVO;
    }
}
