package com.reggie.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.employee.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
