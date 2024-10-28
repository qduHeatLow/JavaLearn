package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {
    /**
     * 新增菜品和对应口味
     * @param dish
     */
    public void saveWithFlavor(DishDTO dish);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id批量删除菜品(一个一个检验然后删除)
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * B站视频里的，根据id批量删除菜品(批量检验是否在套餐里 其实主要是学习where id in 拼接in)
     * @param ids
     */
    void deleteBatchClass(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO queryById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 起售禁售菜品
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类Id查询菜品列表
     *
     * @param categoryId
     * @return
     */
    List<DishVO> listQueryByCategoryId(Long categoryId);


    /**
     * 根据封装了分类Id和在售状态status=1的Dish 查询带有flavor的dish
     * 先查询当前所有符合的dish 然后查询flavor、categoryName封装VO
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
