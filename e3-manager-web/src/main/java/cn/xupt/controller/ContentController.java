package cn.xupt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.content.service.ContentService;
import cn.xupt.pojo.TbContent;

@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult selectConten(long categoryId,Integer page,Integer rows){
		
		EasyUIDataGridResult result = contentService.getContentList(categoryId,page,rows);
		return result;
	}
	
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContent(long categoryId,TbContent tbContent){
		E3Result result = contentService.addContent(tbContent, categoryId);
		return result;
	}
	
	@RequestMapping(value="/content/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteContent(long[] ids){
		E3Result Result = contentService.deleteContent(ids);
		return Result;
			
	}
	
	@RequestMapping(value="/rest/content/edit",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateContent(TbContent tbContent,long categoryId,long id){
		E3Result result = contentService.updateContent(tbContent, categoryId, id);
		return result;
		
	}
}
