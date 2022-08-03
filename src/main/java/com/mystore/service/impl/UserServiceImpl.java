package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.domain.User;
import com.mystore.persistence.UserMapper;
import com.mystore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service("userService") //未来在使用名字为userService的对象时，会更快创建
@Slf4j  //日志框架，可以直接使用log.info()打印
public class UserServiceImpl implements UserService {

    //userMapper会报错，这是Java编译期和运行时的问题，编译器会报错，而运行时Mybatis-Plus会介入，帮助new出对象
    @Autowired
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private Cache<String,String> localCache;

    @Override
    public CommonResponse<User> login(String username, String password) {

        //查询构造器
        User loginUser = userMapper.selectOne(Wrappers.<User>query().eq("username", username));

        //登录失败
        if (loginUser == null) {
            return CommonResponse.createForError("用户名或密码错误");
        }
        boolean checkPassword = bCryptPasswordEncoder.matches(password, loginUser.getPassword());
        //登录成功
        loginUser.setPassword(StringUtils.EMPTY);//将密码信息设为空
        return checkPassword ? CommonResponse.createForSuccess(loginUser) : CommonResponse.createForError("用户名或密码错误");
    }

    @Override
    public CommonResponse<Object> checkField(String fieldName, String fieldValue) {
        //使用StringUtils类进行字符串操作效率更高
        if (StringUtils.equals(fieldName, "username")) {
            long rows = userMapper.selectCount(Wrappers.<User>query().eq("username", fieldValue));
            if (rows > 0) {
                return CommonResponse.createForError("用户名已存在");
            }
        } else if (StringUtils.equals(fieldName, "phone")) {
            long rows = userMapper.selectCount(Wrappers.<User>query().eq("phone", fieldValue));
            if (rows > 0) {
                return CommonResponse.createForError("电话号码已存在");
            }
        } else if (StringUtils.equals(fieldName, "email")) {
            long rows = userMapper.selectCount(Wrappers.<User>query().eq("email", fieldValue));
            if (rows > 0) {
                return CommonResponse.createForError("邮箱已存在");
            }
        } else {
            return CommonResponse.createForError("参数错误");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Object> register(User user) {
        //判断字段是否重复
        CommonResponse<Object> checkResult = checkField("username", user.getUsername());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField("email", user.getEmail());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField("phone", user.getPhone());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));//MD5加密后存入数据库
        user.setRole(CONSTANT.ROLE.CUSTOMER);//普通用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        int rows = userMapper.insert(user);//插入数据
        if (rows == 0) {
            return CommonResponse.createForError("注册用户失败");
        }

        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<User> getUserDetail(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return CommonResponse.createForError("找不到当前用户信息");
        }
        user.setPassword(StringUtils.EMPTY);
        return CommonResponse.createForSuccess(user);
    }

    @Override
    public CommonResponse<String> getForgetQuestion(String username) {
        //判断用户名是否存在
        CommonResponse<Object> checkResult=this.checkField("username",username);
        if(checkResult.isSuccess()){
            return CommonResponse.createForError("该用户名不存在");
        }

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);

        String question=userMapper.selectOne(Wrappers.<User>query().eq("username",username)).getQuestion();
        if(StringUtils.isNotBlank(question)){
            return CommonResponse.createForSuccess(question);
        }

        return CommonResponse.createForError("密码问题为空");
    }

    @Override
    public CommonResponse<String> checkForgetAnswer(String username, String question, String answer) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username).eq("question",question).eq("answer",answer);

        long rows=userMapper.selectCount(queryWrapper);
        //rows>0表示忘记密码的问题答案正确
        if(rows>0){
            //使用UUID生成一段token字符串
            String forgetToken= UUID.randomUUID().toString();
            //将生成的字符串放入本地CaffeineCache缓存中，用户名为key,token为value，失效时间设置为5分钟
            localCache.put(username,forgetToken);
            //输出日志，记录存入缓存成功，打印时间
            log.info("Put into LocalCache: ({},{}), {}",username, forgetToken , LocalDateTime.now());
            return CommonResponse.createForSuccess(forgetToken);
        }
        return CommonResponse.createForError("找回密码的问题答案错误");
    }

    @Override
    public CommonResponse<Object> resetForgetPassword(String username, String newPassword, String forgetToken) {

        CommonResponse<Object> checkResult = this.checkField("username", username);
        if (checkResult.isSuccess()) {
            return CommonResponse.createForError("用户名不存在");
        }
        //从本地缓存中取出之前存入的token
        String token=localCache.getIfPresent(username);
        if(StringUtils.isBlank(token)){//token过期
            return CommonResponse.createForError("token无效或已过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            //对重置的密码进行MD5加密
            String md5Password=bCryptPasswordEncoder.encode(newPassword);

            User user=new User();
            user.setUsername(username);
            user.setPassword(md5Password);

            //更新
            UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("username",username);
            updateWrapper.set("password",md5Password);

            long rows=userMapper.update(user,updateWrapper);
            if(rows>0){
                return CommonResponse.createForSuccess();
            }
            return CommonResponse.createForError("通过忘记密码问题答案，重置密码失败,请重新获取token");
        }else {
            return CommonResponse.createForError("token错误，请重新获取token");
        }
    }

    @Override
    public CommonResponse<Object> resetPassword(String oldPassword, String newPassword, User user) {
        //判断旧密码是否正确
        String password=userMapper.selectById(user.getId()).getPassword();
        boolean checkPassword = bCryptPasswordEncoder.matches(oldPassword, password);
        if(!checkPassword){
            return CommonResponse.createForError("旧密码错误");
        }

        //更新密码
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",user.getId());
        updateWrapper.set("password",user.getPassword());

        long rows=userMapper.update(user,updateWrapper);
        if(rows > 0 ){
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError("密码更新失败");
    }

    @Override
    public CommonResponse<Object> updateUserInfo(User user) {
        //检查更新的Email是否可用
        CommonResponse<Object> checkResult = checkField("email", user.getEmail());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        //检查更新的手机号是否可用
        checkResult = checkField("phone", user.getPhone());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }

        user.setUpdateTime(LocalDateTime.now());//更新时间

        String password=userMapper.selectById(user.getId()).getPassword();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        updateWrapper.set("password", password);
        updateWrapper.set("email", user.getEmail());
        updateWrapper.set("phone", user.getPhone());
        updateWrapper.set("question", user.getQuestion());
        updateWrapper.set("answer", user.getAnswer());
        updateWrapper.set("update_time", user.getUpdateTime());

        long rows = userMapper.update(user, updateWrapper);
        if (rows > 0){//更新成功
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError("更新用户信息失败");
    }
}
