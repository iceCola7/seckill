package me.cxz.seckill.service;

import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.service.model.UserModel;

public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /**
     * @param telephone       用户注册手机
     * @param encryptPassword 加密后的密码
     * @throws BusinessException
     */
    UserModel validateLogin(String telephone, String encryptPassword) throws BusinessException;

}
