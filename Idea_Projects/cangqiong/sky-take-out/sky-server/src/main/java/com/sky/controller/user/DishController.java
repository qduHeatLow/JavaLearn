package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "菜品接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

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



//    /**
//     * 根据分类Id查询菜品列表 这里只有分类Id 所以无法区分admin和user端
//     * @param categoryId
//     * @return
//     */
//    @GetMapping("/list")
//    @ApiOperation("根据分类Id查询菜品列表")
//    public Result<List<DishVO>> listByCategoryId(Long categoryId){
//        List<DishVO> dishList = dishService.listQueryByCategoryId(categoryId);
//        return Result.success(dishList);
//    }

    /**
     * 根据分类Id查询菜品列表 这里只有分类Id 所以无法区分admin和user端
     * 所以课程里是构造了一个Dish对象 封装其分类id 和 Status=1 也就是只能看到在售的
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品列表")
    public Result<List<DishVO>> list(Long categoryId){
        //构造redis的key，规则：dish_分类id
        String key = "dish_"+categoryId;
        //查询redis中是否有对应的菜品们  List<DishVO>
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(dishVOList != null && dishVOList.size() > 0){
            //如果存在 直接返回
            return Result.success(dishVOList);
        }
        //封装Dish查询对象
        Dish dish = new Dish();
        dish.setStatus(StatusConstant.ENABLE);
        dish.setCategoryId(categoryId);

        dishVOList = dishService.listWithFlavor(dish);
        //存入redis
        redisTemplate.opsForValue().set(key,dishVOList);
        return Result.success(dishVOList);
    }
}
