package me.cxz.seckill.service;

import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.service.model.OrderModel;

public interface OrderService {

    // 使用：1.通过前端URL 上传过来秒杀活动ID，然后下单接口内校验对应ID是否属于对应商品且活动已开始
    // 2.直接在下单的接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;

}
