package com.reggie.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.employee.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
