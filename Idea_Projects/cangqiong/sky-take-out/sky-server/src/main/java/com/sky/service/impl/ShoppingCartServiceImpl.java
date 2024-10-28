package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 购物车新增商品
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断当前商品是否已存在 由于我们Mapper设计可以返回多条数据 所以此处用List接收 但是此处只有一条数据List[0]
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        //已存在则数量加1
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            ShoppingCart savedShoppingCart = shoppingCartList.get(0);
            savedShoppingCart.setNumber(savedShoppingCart.getNumber() + 1);
            //更新数据库
            shoppingCartMapper.update(savedShoppingCart);

        }else{
            //不存在插入
            //此处还为空的shoppingCart属性为：name、image、number、amount、createTime

            //判断本次添加到购物车的是套餐还是菜品
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null){
                //菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            }else{
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 购物车商品列表查询
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartList;
    }

    /**
     * 根据用户Id清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 购物车中当前商品减1
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //先查数量
        List<ShoppingCart> savedShoppingCartList = shoppingCartMapper.list(shoppingCart);
        ShoppingCart savedShoppingCart = savedShoppingCartList.get(0);

        savedShoppingCart.setNumber(savedShoppingCart.getNumber() - 1);
        shoppingCartMapper.update(savedShoppingCart);
    }
}
