package com.reggie.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.employee.dto.DishDto;
import com.reggie.employee.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

public interface DishService extends IService<Dish> {
    @Transactional
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    @Transactional
    public  void updateWithFlavor(DishDto dishDto);
}
