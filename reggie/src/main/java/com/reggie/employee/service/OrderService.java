package com.reggie.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.employee.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
