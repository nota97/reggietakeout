package com.reggie.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.employee.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}