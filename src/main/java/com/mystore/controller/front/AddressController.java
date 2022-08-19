package com.mystore.controller.front;

import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.Address;
import com.mystore.domain.User;
import com.mystore.service.AddressService;
import com.mystore.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/address/")
@Validated
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("add_address")
    public CommonResponse<AddressVO> addAddress(
            @RequestBody @Valid Address address,
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.addAddress(address, loginUser.getId());
    }

    @PostMapping("delete_address")
    public CommonResponse<Object> deleteAddress(
            HttpSession session,
            @RequestParam @NotNull(message = "地址ID不能为空") Integer addressId
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.deleteAddress(addressId,loginUser.getId());
    }

    @PostMapping("update_address")
    public CommonResponse<AddressVO> updateAddress(
            @RequestBody @Valid Address address,
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.updateAddress(loginUser.getId(),address);
    }

    @GetMapping("find")
    public CommonResponse<AddressVO> findById(
            HttpSession session,
            @RequestParam @NotNull(message = "地址ID不能为空") Integer addressId
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.findById(loginUser.getId(),addressId);
    }

    @GetMapping("find_list")
    public CommonResponse<List<AddressVO>> findList(HttpSession session){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.findAll(loginUser.getId());
    }
}
