package me.cxz.seckill.service.impl;

import me.cxz.seckill.dao.ItemDOMapper;
import me.cxz.seckill.dao.ItemStockDOMapper;
import me.cxz.seckill.dataobject.ItemDO;
import me.cxz.seckill.dataobject.ItemStockDO;
import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.error.EmBusinessError;
import me.cxz.seckill.service.ItemService;
import me.cxz.seckill.service.PromoService;
import me.cxz.seckill.service.model.ItemModel;
import me.cxz.seckill.service.model.PromoModel;
import me.cxz.seckill.validator.ValidationResult;
import me.cxz.seckill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDoMapper;

    @Autowired
    private ItemStockDOMapper itemStockDoMapper;

    @Autowired
    private PromoService promoService;

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDo = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDo);
        itemDo.setPrice(itemModel.getPrice().doubleValue());
        return itemDo;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDo = new ItemStockDO();
        itemStockDo.setStock(itemModel.getStock());
        itemStockDo.setItemId(itemModel.getId());
        return itemStockDo;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {

        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrorMsg());
        }

        // 转化 itemmode->dataobject
        ItemDO itemDo = convertItemDOFromItemModel(itemModel);

        // 写入数据库
        itemDoMapper.insertSelective(itemDo);

        itemModel.setId(itemDo.getId());

        ItemStockDO itemStockDo = convertItemStockDOFromItemModel(itemModel);
        itemStockDoMapper.insertSelective(itemStockDo);

        // 返回创建完的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {

        List<ItemDO> itemDoList = itemDoMapper.listItem();

        List<ItemModel> itemModelList = itemDoList.stream().map(itemDo -> {
            ItemStockDO itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDo, itemStockDo);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {

        ItemDO itemDo = itemDoMapper.selectByPrimaryKey(id);
        if (itemDo == null) {
            return null;
        }

        // 操作获得库存数量
        ItemStockDO itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());

        // 将 dataobject->model
        ItemModel itemModel = this.convertModelFromDataObject(itemDo, itemStockDo);

        // 获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus().intValue() != 3) {
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectedRow = itemStockDoMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0) {
            // 更新库存成功
            return true;
        } else {
            // 更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDoMapper.increaseSales(itemId, amount);
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDo, ItemStockDO itemStockDo) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDo, itemModel);
        itemModel.setPrice(new BigDecimal(itemDo.getPrice()));
        itemModel.setStock(itemStockDo.getStock());
        return itemModel;
    }

}
