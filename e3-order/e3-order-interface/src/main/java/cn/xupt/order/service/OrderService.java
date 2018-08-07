package cn.xupt.order.service;

import cn.xupt.common.utils.E3Result;
import cn.xupt.order.pojo.OrderInfo;

public interface OrderService {

	public E3Result createOrder(OrderInfo orderInfo);
}
