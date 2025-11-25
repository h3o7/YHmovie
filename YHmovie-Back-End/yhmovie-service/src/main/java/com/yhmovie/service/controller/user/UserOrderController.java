package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.vo.OrdersVo;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/orders")
public class UserOrderController {
    private final IOrdersService ordersService;

    @PostMapping("/buy")
    public Result buyTickets(@RequestBody List<String> showtimesIds){
        return ordersService.buyTickets(showtimesIds);
    }

    @PostMapping("/return/{orderId}")
    public Result returnTickets(@PathVariable String orderId){
        return ordersService.returnTickets(orderId);
    }

    @GetMapping("/list")
    public Result userOrders(){
        List<OrdersVo> list = ordersService.userOrders();
        if(list == null || list.isEmpty()){
            return Result.error("暂无订单");
        }
        return Result.success(list);
    }
}
