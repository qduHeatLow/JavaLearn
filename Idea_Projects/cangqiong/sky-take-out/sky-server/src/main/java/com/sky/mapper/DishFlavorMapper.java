package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入菜品口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品Id删除菜品口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);

    /**
     * 根据多个菜品Id删除菜品口味
     * @param ids
     */
    void deleteBatchByDishIds(List<Long> ids);

    /**
     * 根据菜品Id查询该菜品对应的口味
     * @param id
     * @return List<DishFlavor> 对应的口味列表
     */
    @Select("select * from dish_flavor where dish_id = #{id};")
    List<DishFlavor> getFlavorsByDishId(Long id);


}
