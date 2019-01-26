package me.cxz.seckill.dao;

import me.cxz.seckill.dataobject.ItemDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    List<ItemDo> listItem();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    int insert(ItemDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    int insertSelective(ItemDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    ItemDo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    int updateByPrimaryKeySelective(ItemDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Fri Jan 25 14:08:19 CST 2019
     */
    int updateByPrimaryKey(ItemDo record);

    int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);
}