package com.reggie.employee.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.employee.common.Result;
import com.reggie.employee.dto.DishDto;
import com.reggie.employee.entity.Category;
import com.reggie.employee.entity.Dish;
import com.reggie.employee.entity.DishFlavor;
import com.reggie.employee.service.CategoryService;
import com.reggie.employee.service.DishFlavorService;
import com.reggie.employee.service.DishService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return Result.success("新增菜品成功");
    }

    /**
     * 分页显示菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        //遍历records中的每天信息，保存分类名称categoryName
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName =category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    /**
     * 编辑页面根据id获取单个菜品信息
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return Result.success("修改菜品信息成功");
    }


    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtos = null;
        //判断redis缓存中是否存在，存在则读取缓存中的数据
        String key = "dish_"+ dish.getCategoryId()+"_"+dish.getStatus();
        dishDtos = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if (dishDtos != null){
            return Result.success(dishDtos);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(dish.getName())){
            queryWrapper.like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName());
        }else {
            queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        }
        queryWrapper.eq(Dish::getStatus,1);

        List<Dish> dishes = dishService.list(queryWrapper);
        dishDtos = dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            dishDto.setFlavors(dishFlavorService.list(lambdaQueryWrapper));
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtos,1, TimeUnit.DAYS);
        return Result.success(dishDtos);
    }


}
