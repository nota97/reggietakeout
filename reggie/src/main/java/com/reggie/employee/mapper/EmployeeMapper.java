package com.reggie.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.employee.entity.Employee;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
