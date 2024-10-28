package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品总数
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Integer categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品、并查询种类名
     * @param queryDTO
     * @return
     */
    //SELECT d.*,c.name AS category_name FROM dish d left outer JOIN category c on d.category_id = c.id
    Page<DishVO> pageQuery(DishPageQueryDTO queryDTO);

    /**
     * 根据主键查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键ID删除菜品
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据多个主键ID批量删除菜品
     * @param ids
     */
    void deleteBatchByIds(List<Long> ids);

    /**
     * 更新菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类Id查询菜品列表
     * @param categoryId
     * @return
     */
    @Select("select * from dish  where category_id = #{categoryId}")
    List<Dish> listQueryByCategoryId(Long categoryId);

    /**
     * 根据封装条件查询dish
     * @param dishforQuery
     * @return
     */
    List<Dish> list(Dish dishforQuery);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
