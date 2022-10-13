package com.reggie.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.employee.dto.SetmealDto;
import com.reggie.employee.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
