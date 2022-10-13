package com.reggie.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.employee.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
