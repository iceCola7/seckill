package me.cxz.seckill.controller;

import me.cxz.seckill.controller.viewobject.OrderVO;
import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.error.EmBusinessError;
import me.cxz.seckill.response.CommonReturnType;
import me.cxz.seckill.service.OrderService;
import me.cxz.seckill.service.model.OrderModel;
import me.cxz.seckill.service.model.UserModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域请求的问题
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam("itemId") Integer itemId,
                                        @RequestParam("amount") Integer amount,
                                        @RequestParam("promoId") Integer promoId) throws BusinessException {

        UserModel userModel = validateLogin();

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);

        return CommonReturnType.create(orderModel);
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listOrderByUserId() throws BusinessException {

        UserModel userModel = validateLogin();

        List<OrderModel> orderModelList = orderService.listOrderByUserId(userModel.getId());

        List<OrderVO> orderVOList = orderModelList.stream().map(orderModel -> {
            OrderVO orderVO = convertFromModel(orderModel);
            return orderVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(orderVOList);
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getOrder(@RequestParam("id") String id) throws BusinessException {

        validateLogin();

        OrderModel orderModel = orderService.getOrderById(id);
        OrderVO orderVO = this.convertFromModel(orderModel);

        return CommonReturnType.create(orderVO);
    }

    /**
     * 验证是否已经登录
     *
     * @return 返回用户对象
     * @throws BusinessException 没有登录抛异常
     */
    private UserModel validateLogin() throws BusinessException {
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        // 获取用户的登录信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        return userModel;
    }

    private OrderVO convertFromModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderModel, orderVO);
        orderVO.setCreateTime(orderModel.getCreateTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));

        return orderVO;
    }

}
