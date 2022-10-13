package com.reggie.employee.dto;

import com.reggie.employee.entity.OrderDetail;
import com.reggie.employee.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
