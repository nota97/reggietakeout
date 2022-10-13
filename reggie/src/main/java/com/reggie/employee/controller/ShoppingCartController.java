package com.reggie.employee.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.employee.common.BaseContext;
import com.reggie.employee.common.Result;
import com.reggie.employee.entity.ShoppingCart;
import com.reggie.employee.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        return Result.success(cartList);
    }

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车信息{}",shoppingCart);
        //设置当前用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        //判断添加的是菜品还是套餐
        if(shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询表中是否已经有该菜品或套餐
        ShoppingCart foodInCat = shoppingCartService.getOne(queryWrapper);
        if(foodInCat != null){
            //已存在则数量加一
            int num = foodInCat.getNumber();
            foodInCat.setNumber(num+1);
            shoppingCartService.updateById(foodInCat);
        }else{
            //不存在则新插入数据
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            foodInCat = shoppingCart;
        }
        return Result.success(foodInCat);
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("清空购物车成功");
    }


    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId()!=null){
            //判断是菜品信息
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            //判断是套餐信息
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //获取需要数量减一的信息
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        int num = cartServiceOne.getNumber();
        if(num>1){
            cartServiceOne.setNumber(num-1);
            shoppingCartService.updateById(cartServiceOne);
            return Result.success(cartServiceOne);
        } else if (num ==1) {
            cartServiceOne.setNumber(0);
            shoppingCartService.remove(queryWrapper);
            return Result.success(cartServiceOne);
        }else{
            return Result.error("请求失败，请稍后重试");
        }
    }
}
