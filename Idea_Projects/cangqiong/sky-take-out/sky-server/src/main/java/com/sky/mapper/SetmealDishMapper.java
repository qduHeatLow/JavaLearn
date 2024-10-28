package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id，查询所有套餐里该菜品出现的次数
     * @param id
     * @return
     */
    @Select("select count(dish_id) from setmeal_dish")
    Long countByDishId(Long id);

    /**
     * 根据多个菜品Id查询对应的多个套餐Id
     * @param ids
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in ids
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 根据套餐Id查询改套餐下的所有菜品
     *
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmeal_id}")
    List<SetmealDish> getSetmealDishesBySetmealId(Long setmeal_id);

    /**
     * 批量插入
     *
     * @param setmealId
     * @param setmealDishes
     */
    void insertBatch(Long setmealId, List<SetmealDish> setmealDishes);

    /**
     * 根据套餐Id查询DishItem信息 两个表
     * @param id
     * @return
     */
    @Select("select sd.name,sd.copies,d.image,d.description from setmeal_dish sd left outer join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{id}")
    List<DishItemVO> getDishItemsBySetmealId(Long id);

    /**
     * 根据套餐Id批量删除套餐菜品关系，供直接插入（update）
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBatchBySetmealId(Long id);
}
