package cn.xupt.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.pojo.TbItem;
import cn.xupt.pojo.TbItemDesc;
import cn.xupt.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem geTbItemById(@PathVariable Long itemId){
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	/**
	 * 商品添加功能
	 */
	
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem item,String desc){
		E3Result result = itemService.addItem(item, desc);
		return result;
	}
	/*
	 * 删除
	 * */
	
	@RequestMapping(value="/rest/item/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteItem(@RequestParam("ids") long []itemId){
		E3Result result = itemService.deleteItem(itemId);
		return result;
	}
	
	/*
	 * 下架
	 * */
	@RequestMapping(value="/rest/item/reshelf",method=RequestMethod.POST)
	@ResponseBody
	public E3Result editStatusReshelf(@RequestParam("ids") long []itemId){
		E3Result result = itemService.upperoffItem(itemId);
		return result;
	}
	/*
	 * 上架
	 * */
	
	@RequestMapping(value="/rest/item/instock",method=RequestMethod.POST)
	@ResponseBody
	public E3Result editStatusInstock(@RequestParam("ids") long []itemId){
		E3Result result = itemService.dropoffItem(itemId);
		return result;
	}

	/**
	 * 异步重新加载回显描述
	 * @param id
	 * @return
	 */
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result selectTbItemDesc(@PathVariable long id){
		E3Result result= itemService.selectTbItemDesc(id);
		return result;
	}
	/**
	 * 异步重新加载商品信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/rest/item/param/item/query/{id}")
	@ResponseBody
	public TbItem queryById(@PathVariable long id){
	    TbItem item = itemService.getItemById(id);
	    return item;
	}
	
	@RequestMapping(value="/rest/item/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateItem(long id,TbItem item,String desc){
		E3Result result = itemService.updateItem(id,item, desc);
		return result;
	}
	
}
