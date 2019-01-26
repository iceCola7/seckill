package me.cxz.seckill.service.impl;

import me.cxz.seckill.dao.PromoDoMapper;
import me.cxz.seckill.dataobject.PromoDo;
import me.cxz.seckill.service.PromoService;
import me.cxz.seckill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDoMapper promoDoMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {

        // 获取对应商品的秒杀活动信息
        PromoDo promoDo = promoDoMapper.selectByItemId(itemId);

        // dataobject->model
        PromoModel promoModel = this.convertFromDataObject(promoDo);

        if (promoModel == null) {
            return null;
        }

        // 判断当前时间是否秒杀活动即将开始或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDo promoDo) {
        if (promoDo == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDo, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDo.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDo.getEndDate()));
        return promoModel;
    }

}
