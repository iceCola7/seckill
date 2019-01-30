package me.cxz.seckill.service.impl;

import me.cxz.seckill.dao.OrderDOMapper;
import me.cxz.seckill.dao.SequenceDOMapper;
import me.cxz.seckill.dataobject.OrderDO;
import me.cxz.seckill.dataobject.SequenceDO;
import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.error.EmBusinessError;
import me.cxz.seckill.service.ItemService;
import me.cxz.seckill.service.OrderService;
import me.cxz.seckill.service.UserService;
import me.cxz.seckill.service.model.ItemModel;
import me.cxz.seckill.service.model.OrderModel;
import me.cxz.seckill.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDoMapper;

    @Autowired
    private SequenceDOMapper sequenceDoMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {

        // 1.校验下单状态，下单商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }

        // 校验活动信息
        if (promoId != null) {
            // （1）校验对应活动是否存在这个适用商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不存在");
                // （2）校验活动是否正在进行中
            } else if ((itemModel.getPromoModel().getStatus().intValue() != 2)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息还未开始");
            }
        }

        // 2.落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setUserId(userId);
        orderModel.setAmount(amount);
        orderModel.setCreateTime(new DateTime());
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        // 生成订单号
        orderModel.setId(generateOrderNo());
        OrderDO orderDo = this.convertFromOrderModel(orderModel);
        orderDoMapper.insertSelective(orderDo);

        // 加上商品的销量
        itemService.increaseSales(itemId, amount);

        // 4.返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo() {

        // 订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        // 前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        // 中间6位为自增序列
        // 获取当前 sequence
        int sequence = 0;
        SequenceDO sequenceDo = sequenceDoMapper.getSequenceByName("order_info");
        sequence = sequenceDo.getCurrentValue();
        sequenceDo.setCurrentValue(sequenceDo.getCurrentValue() + sequenceDo.getStep());
        sequenceDoMapper.updateByPrimaryKeySelective(sequenceDo);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        // 最后2位为分库分表位，暂时写死
        stringBuilder.append("00");

        return String.valueOf(stringBuilder);

    }

    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDO orderDo = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDo);
        orderDo.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDo.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        orderDo.setCreateTime(orderModel.getCreateTime().toDate());
        return orderDo;
    }

}
