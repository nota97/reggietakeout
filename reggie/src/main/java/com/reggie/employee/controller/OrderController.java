package com.reggie.employee.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.employee.common.Result;
import com.reggie.employee.dto.OrdersDto;
import com.reggie.employee.entity.OrderDetail;
import com.reggie.employee.entity.Orders;
import com.reggie.employee.service.OrderDetailService;
import com.reggie.employee.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("订单信息{}", orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }

    @GetMapping("/userPage")
    public Result<Page> userPage(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        orderService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> dtoRecords = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail> qWrapper = new LambdaQueryWrapper<>();
            qWrapper.eq(OrderDetail::getOrderId,item.getId());
            ordersDto.setOrderDetails(orderDetailService.list(qWrapper));
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoRecords);
        return Result.success(dtoPage);
    }

    /**
     * 后台订单明细页面
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> Page(int page, int pageSize, String number, String beginTime,String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //判断是否传入时间参数，比较时间
        if (beginTime !=null && endTime != null){
            LocalDateTime nBeginTime= LocalDateTime.parse(beginTime,df);
            LocalDateTime nEndTime= LocalDateTime.parse(endTime,df);
            log.info("时间 {}--{}",nBeginTime,nEndTime);
            queryWrapper.between(Orders::getCheckoutTime,nBeginTime,nEndTime);
        }

//
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        orderService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> dtoRecords = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail> qWrapper = new LambdaQueryWrapper<>();
            qWrapper.eq(OrderDetail::getOrderId,item.getId());
            ordersDto.setOrderDetails(orderDetailService.list(qWrapper));
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoRecords);
        return Result.success(dtoPage);
    }

    @PutMapping
    public Result<String> updateStatus(@RequestBody Orders orders){
        log.info("订单信息{}",orders);
        Long orderId = orders.getId();
        Orders orders1 = orderService.getById(orderId);
        orders1.setStatus(orders.getStatus());
        orderService.updateById(orders1);
        return Result.success("修改订单状态成功");
    }
}
