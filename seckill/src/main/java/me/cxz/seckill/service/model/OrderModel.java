package me.cxz.seckill.service.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * 用户下单的交易模型
 */
public class OrderModel {

    private String id;

    // 购买的用户 id
    private Integer userId;

    // 购买的商品 id
    private Integer itemId;

    // 若非空，则以秒杀商品的方式下单
    private Integer promoId;

    // 购买商品的单价，若 promoId 非空，则表示秒杀商品价格
    private BigDecimal itemPrice;

    // 购买的数量
    private Integer amount;

    // 购买的金额，若 promoId 非空，则表示秒杀商品价格
    private BigDecimal orderPrice;

    private DateTime createTime;

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
