package com.reggie.employee.dto;


import com.reggie.employee.entity.Setmeal;
import com.reggie.employee.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //套餐中的菜品信息列表
    private List<SetmealDish> setmealDishes;

    //套餐分类名称
    private String categoryName;
}
