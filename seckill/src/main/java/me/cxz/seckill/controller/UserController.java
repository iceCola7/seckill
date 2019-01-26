package me.cxz.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import me.cxz.seckill.controller.viewobject.UserVO;
import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.error.EmBusinessError;
import me.cxz.seckill.response.CommonReturnType;
import me.cxz.seckill.service.UserService;
import me.cxz.seckill.service.model.UserModel;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域请求的问题
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 用户登录接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam("telephone") String telephone,
                                  @RequestParam("password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        // 入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) || org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 用户登录服务，用来校验参数是否合法
        UserModel userModel = userService.validateLogin(telephone, this.encodeByMd5(password));

        // 将用户登录凭证加入到用户登录成功的 session 内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }


    // 用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam("telephone") String telephone,
                                     @RequestParam("otpCode") String otpCode,
                                     @RequestParam("name") String name,
                                     @RequestParam("gender") Integer gender,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        // 验证手机号和对应的 otpcode 是否符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);

        if (!StringUtils.equals(inSessionOtpCode, otpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不正确");
        }

        // 用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender.byteValue());
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncryptPassword(this.encodeByMd5(password));
        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    public String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));
        return newStr;
    }

    // 用户获取 otp 短息接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam("telephone") String telephone) {

        // 需要按照一定的规则生成 OTP 验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // 将 OTP 验证码通对应用户的手机号关联，使用 httpsession 的方式绑定手机号和 OTPCode
        httpServletRequest.getSession().setAttribute(telephone, otpCode);

        // 将 OTP 验证码通过短息通道发送给用户，省略
        System.out.println("telephone = " + telephone + " & otpCode = " + otpCode);

        return CommonReturnType.create(null);

    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam("id") Integer id) throws BusinessException {
        // 调用 Service 服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        // 若获取的对应用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        // 将核心领域模型用户对象转化为可供UI使用的ViewObject
        UserVO userVO = convertFromModel(userModel);

        // 返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

}
