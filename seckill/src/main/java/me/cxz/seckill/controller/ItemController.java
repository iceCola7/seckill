package me.cxz.seckill.controller;

import me.cxz.seckill.controller.viewobject.ItemVO;
import me.cxz.seckill.error.BusinessException;
import me.cxz.seckill.response.CommonReturnType;
import me.cxz.seckill.service.ItemService;
import me.cxz.seckill.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域请求的问题
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    // 创建商品
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       @RequestParam("price") BigDecimal price,
                                       @RequestParam("stock") Integer stock,
                                       @RequestParam("imageUrl") String imageUrl) throws BusinessException {

        // 封装 Service 请求用来创建的商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImageUrl(imageUrl);

        ItemModel itemModelR = itemService.createItem(itemModel);

        ItemVO itemVO = convertVOFromModel(itemModelR);

        return CommonReturnType.create(itemVO);
    }

    // 商品详情页浏览
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam("id") Integer id) {
        ItemModel itemModel = itemService.getItemById(id);

        ItemVO itemVO = convertVOFromModel(itemModel);

        return CommonReturnType.create(itemVO);
    }

    // 商品列表页面浏览
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType list() {
        List<ItemModel> itemModelList = itemService.listItem();

        // 使用 stream api 将 list 内的 ItemModel 转化为 ItemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }


    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);

        if (itemModel.getPromoModel() != null) {
            // 有即将进行或正在进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }

        return itemVO;
    }

}

