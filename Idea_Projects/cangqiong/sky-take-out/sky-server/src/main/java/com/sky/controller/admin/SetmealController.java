package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 根据主键Id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> queryById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.queryById(id);
        return Result.success(setmealVO);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据主键Id批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping()
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @ApiOperation("根据主键Id批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        setmealService.deleteBatchByIds(ids);
        return Result.success();
    }

    /**
     * 更新套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping()
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @ApiOperation("更新套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 起售禁售套餐
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @ApiOperation("起售禁售套餐")
    public Result startOrStop(@PathVariable Integer status,Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据分类Id查询套餐列表 admin端 不用封装status
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询套餐列表")
    public Result<List<Setmeal>> listByCategoryId(Long categoryId){
        List<Setmeal> setmealList = setmealService.listQueryByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    /**
     * 根据套餐Id查询包含的菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐Id查询包含的菜品")
    public Result<List<SetmealDish>> dish(@PathVariable Long id){
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishesBySetmealId(id);
        return Result.success(setmealDishes);
    }
}
