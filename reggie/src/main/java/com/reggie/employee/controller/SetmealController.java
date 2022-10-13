package com.reggie.employee.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.employee.common.Result;
import com.reggie.employee.dto.SetmealDto;
import com.reggie.employee.entity.Category;
import com.reggie.employee.entity.Setmeal;
import com.reggie.employee.entity.SetmealDish;
import com.reggie.employee.service.CategoryService;
import com.reggie.employee.service.DishService;
import com.reggie.employee.service.SetmealDishService;
import com.reggie.employee.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Result.success("新增套餐成功");
    }


    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
//        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.eq(Setmeal::getIsDeleted,0);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);

        //拷贝对象
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //遍历records中信息，添加CategoryName信息，并保存至dto
        List<SetmealDto> setmealDtos =records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);
        return Result.success(setmealDtoPage);
    }

    /**
     * 编辑页面获取单个id 信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return Result.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return Result.success("修改套餐信息成功");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return Result.success("删除成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("套餐信息 {},{}",status,ids);
        ids.stream().map((item)->{
            Setmeal setmeal = setmealService.getById(item);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
            return item;
        }).collect(Collectors.toList());

        return Result.success("套餐成功修改状态");
    }

    @GetMapping("/list")
    @Cacheable(value="setmealCache",key = "#setmeal.categoryId+'_'+ #setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        log.info("套餐信息{}",setmeal);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return Result.success(setmealList);
    }
}
