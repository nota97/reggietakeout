package com.reggie.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.employee.entity.Dish;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
