package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //自动把没有传过来的status赋值为0，初始禁售

        //原本dish是没有主键的，而是SQL自动递增顺序生成的
        //使用主键回显之后，dish对象的主键值就被自动调用set方法注入了
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {flavor.setDishId(dishId);});
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品 不需要口味
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据id批量删除菜品(实际一个一个判断然后删除)
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        //查看是否起售
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish != null){
                continue;
            }
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //查看是否在套餐中
            Long count = setmealDishMapper.countByDishId(id);
            //在套餐中
            if(count > 0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }

            //不在套餐中 可以删除 但是先删除口味
            dishFlavorMapper.deleteByDishId(id);
            dishMapper.deleteById(id);
        }

    }

    /**
     * B站视频里的，根据id批量删除菜品(批量检验是否在套餐里 其实主要是学习where id in 拼接in)
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchClass(List<Long> ids) {
        //查看是否起售
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //查看是否有套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据以及菜品口味
        dishMapper.deleteBatchByIds(ids);
        dishFlavorMapper.deleteBatchByDishIds(ids);

    }

    /**
     * 根据Id查询菜品（包括关联的分类和口味信息）
     * @param id
     * @return
     */
    @Override
    public DishVO queryById(Long id) {
        Dish dish = dishMapper.getById(id);
        if(dish == null){
            throw new BaseException("菜品为空");
        }
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
//        //分类名称
//        private String categoryName;

        Category categoryOfDish = categoryMapper.getById(dish.getCategoryId());
        dishVO.setCategoryName(categoryOfDish.getName());

//        //菜品关联的口味
//        private List<DishFlavor> flavors = new ArrayList<>();
        List<DishFlavor> flavors = dishFlavorMapper.getFlavorsByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //更新dish
        dishMapper.update(dish);

        //更新flavor 其name唯一
//        //口味
//        private List<DishFlavor> flavors = new ArrayList<>();
        dishFlavorMapper.deleteByDishId(dish.getId());
        //向口味表插入n条数据，为什么不直接插？因为可能flavor的dishId前端没有封装，需要在这里封装
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {flavor.setDishId(dishDTO.getId());});
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 起售禁售菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据分类Id查询菜品列表  admin端用
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> listQueryByCategoryId(Long categoryId) {
        List<DishVO> dishVOList = new ArrayList<>();
        List<Dish> dishList = dishMapper.listQueryByCategoryId(categoryId);
        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            List<DishFlavor> dishFlavorList= dishFlavorMapper.getFlavorsByDishId(dish.getId());
            dishVO.setFlavors(dishFlavorList);
            dishVO.setCategoryName(categoryMapper.getById(dish.getCategoryId()).getName());

            dishVOList.add(dishVO);
        });


        return dishVOList;
    }

    /**
     * 根据封装了分类Id和在售状态status=1的Dish 查询带有flavor的dish
     * 先查询当前所有符合的dish 然后查询flavor、categoryName封装VO
     * @param dishForQuery
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dishForQuery) {
        List<Dish> dishList = dishMapper.list(dishForQuery);

        List<DishVO> dishVOList = new ArrayList<>();

        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            List<DishFlavor> dishFlavorList= dishFlavorMapper.getFlavorsByDishId(dish.getId());
            dishVO.setFlavors(dishFlavorList);
            dishVO.setCategoryName(categoryMapper.getById(dish.getCategoryId()).getName());

            dishVOList.add(dishVO);
        });


        return dishVOList;
    }


}
