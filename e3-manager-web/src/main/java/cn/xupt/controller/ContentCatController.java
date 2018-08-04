package cn.xupt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.xupt.common.pojo.EasyUITreeNode;
import cn.xupt.common.utils.E3Result;
import cn.xupt.content.service.ContentCategoryService;

/**
 * 内容分类管理
 * <p>Title: ContentCatController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ContentCatController {

	@Autowired
	private ContentCategoryService contentCategoryService;


	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(@RequestParam
			(name="id",defaultValue="0")long prentId){
		
		List<EasyUITreeNode> list = contentCategoryService.getContentCatList(prentId);
		return list;
	}
	
	/*
	 * 添加分类节点
	 * */
	@RequestMapping(value="/content/category/create",method=RequestMethod.POST)
	@ResponseBody
	public E3Result createContentCategory(Long parentId,String name){
		
		//调用服务添加节点
		E3Result result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	/*@RequestMapping(value="/content/category/delete/",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteContentCategory(Long id){
		
		//调用服务添加节点
		E3Result result = contentCategoryService.deleteContentCategory(id);
		return result;
	}
	
	*/
	@RequestMapping(value="/content/category/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateContentCategory(Long id,String name){
		
		//调用服务添加节点
		E3Result result = contentCategoryService.updateContentCategory(id, name);
		return result;
	}
	
	
}
