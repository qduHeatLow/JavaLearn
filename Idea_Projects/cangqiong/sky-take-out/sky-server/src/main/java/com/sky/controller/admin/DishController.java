package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api(tags = "菜品接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增菜品")
    public Result add(@RequestBody DishDTO dishDTO) {
        dishService.saveWithFlavor(dishDTO);

//        //清除缓存数据
//        String key = "dish_" + dishDTO.getCategoryId();
//        redisTemplate.delete(key);

        //新增默认停售 所以不用清除缓存
        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping("")
    @ApiOperation("根据id批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.deleteBatch(ids);

        //清除缓存数据 将所有的缓存数据删掉 使用keys 匹配所有dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> queryById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO = dishService.queryById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping("")
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);

        //清除缓存数据 将所有的缓存数据删掉 使用keys 匹配所有dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 起售禁售菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售禁售菜品")
    public Result startOrStop(@PathVariable Integer status,Long id){
        dishService.startOrStop(status,id);

        //清除缓存数据 将所有的缓存数据删掉 使用keys 匹配所有dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类Id查询菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品列表")
    public Result<List<DishVO>> listByCategoryId(Long categoryId){
        List<DishVO> dishList = dishService.listQueryByCategoryId(categoryId);
        return Result.success(dishList);
    }

    /**
     * 清除缓存数据 将所有的缓存数据删掉 使用keys 匹配所有dish_开头的key
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
