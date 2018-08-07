package cn.xupt.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.xupt.cart.service.CartService;
import cn.xupt.common.utils.E3Result;
import cn.xupt.order.pojo.OrderInfo;
import cn.xupt.order.service.OrderService;
import cn.xupt.pojo.TbItem;
import cn.xupt.pojo.TbUser;

@Controller
public class OrderController {

	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		//取用户id
		TbUser user = (TbUser) request.getAttribute("user");
		//根据用户id取收货地址
		//取支付方式
		//根据用户id取购物车列表
		List<TbItem> cartList= cartService.getCartList(user.getId());
		//把购物车传递给jsp
		request.setAttribute("cartList", cartList);
		//返回页面
		return "order-cart";
		
	}
	
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, HttpServletRequest requests){
		//取用户信息
		TbUser user = (TbUser) requests.getAttribute("user");
		//把用户信息添加到orderInfo中。
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		//调用服务生成订单号
		E3Result e3Result = orderService.createOrder(orderInfo);
		//如果订单生成成功，需要删除购物车
		if(e3Result.getStatus() == 200){
			//清空购物车
			cartService.clearCartItem(user.getId());
		}
		//把订单号传递给页面
		requests.setAttribute("orderId", e3Result.getData());
		requests.setAttribute("payment", orderInfo.getPayment());
		//返回逻辑视图
		return "success";
	}
}
