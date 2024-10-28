package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService {

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据主键Id批量删除套餐
     * @param ids
     */
    void deleteBatchByIds(List<Long> ids);

    /**
     * 根据主键Id查询套餐
     * @param id
     * @return
     */
    SetmealVO queryById(Long id);

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 更新套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 起售禁售套餐
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类Id查询套餐列表 admin端 不用封装status
     * @param categoryId
     * @return
     */
    List<Setmeal> listQueryByCategoryId(Long categoryId);

    /**
     * 根据分类Id查询套餐列表 user端，需要封装status
     * @param categoryId
     * @return
     */
    List<Setmeal> list(Long categoryId);

    /**
     * 根据套餐Id查询包含的菜品
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemByid(Long id);
}
