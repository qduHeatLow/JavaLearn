package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据主键Id批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatchByIds(List<Long> ids) {
        //起售中的没法删
        ids.forEach(id->{
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        //如果可以的话 批量删除这一批
        setmealMapper.deleteBatchByIds(ids);
    }

    /**
     * 根据主键Id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO queryById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);

        Category category = categoryMapper.getById(setmeal.getCategoryId());
        String categoryName = category.getName();
        if(categoryName!=null){
            setmealVO.setCategoryName(categoryName);
        }
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishesBySetmealId(id);
        if(setmealDishes!= null && !setmealDishes.isEmpty()){
            setmealVO.setSetmealDishes(setmealDishes);
        }
        return setmealVO;
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //自动把没有传过来的status赋值为0，初始禁售
        setmealMapper.insert(setmeal);
        //DTO中含有套餐菜品关系，需要存储到setmeal_dish表中
        //    private List<SetmealDish> setmealDishes = new ArrayList<>();


//        setmealDTO.getSetmealDishes().forEach(setmealDish->{
//            setmealDishMapper.insert(setmealDish);
//        });
        setmealDishMapper.insertBatch(setmealDTO.getId(), setmealDTO.getSetmealDishes());

    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //直接删除该套餐下的所有菜品，然后批量插入选择的
        setmealDishMapper.deleteBatchBySetmealId(setmealDTO.getId());
        setmealDishMapper.insertBatch(setmealDTO.getId(),setmealDTO.getSetmealDishes());
    }

    /**
     * 起售禁售套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类Id查询套餐列表 admin端 不用封装status
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> listQueryByCategoryId(Long categoryId) {
        return setmealMapper.listQeuryByCategoryId(categoryId);
    }

    /**
     * 根据分类Id查询套餐列表 user端，需要封装status
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmealList = setmealMapper.list(setmeal);
        return setmealList;
    }

    /**
     * 根据套餐Id查询包含的菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemByid(Long id) {
        List<DishItemVO> dishItemVOList = new ArrayList<>();
        dishItemVOList = setmealDishMapper.getDishItemsBySetmealId(id);
        return dishItemVOList;
    }
}
