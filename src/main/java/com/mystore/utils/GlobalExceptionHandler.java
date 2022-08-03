package com.mystore.utils;

import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;

//定义全局异常类
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(code= HttpStatus.BAD_REQUEST)   //指定状态码
    public CommonResponse<Object> handleMissingParamException(){
        return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());//参数异常
    }

    //非对象参数校验
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR)   //指定状态码
    public CommonResponse<Object> handleViolationException(ConstraintViolationException exception){
        return CommonResponse.createForError(exception.getMessage());
    }

    //对象参数校验（使用@Valid注解后可以显示响应的message）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(code= HttpStatus.BAD_REQUEST)   //指定状态码
    public CommonResponse<Object> handleValidException(MethodArgumentNotValidException exception){
        return CommonResponse.createForError(
                ResponseCode.ARGUMENT_ILLEGAL.getCode(), formatValidErrorsMessage(exception.getAllErrors()));
    }

    //处理MethodArgumentValidException异常的message不好用的问题，格式化之后返回
    private String formatValidErrorsMessage(List<ObjectError> errorList){
        StringBuffer errorMessage=new StringBuffer();
        errorList.forEach(error->errorMessage.append(error.getDefaultMessage()).append(","));
        errorMessage.deleteCharAt(errorMessage.length()-1);
        return errorMessage.toString();
    }
}
