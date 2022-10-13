package com.reggie.employee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.employee.entity.SetmealDish;
import com.reggie.employee.mapper.SetmealDishMapper;
import com.reggie.employee.service.SetmealDishService;
import org.springframework.stereotype.Service;


@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
