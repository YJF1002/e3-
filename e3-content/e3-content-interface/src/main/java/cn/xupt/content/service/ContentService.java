package cn.xupt.content.service;

import java.util.List;

import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.pojo.TbContent;

public interface ContentService {

	EasyUIDataGridResult getContentList(long categoryId,int page, int rows);
	E3Result deleteContent(long[] ids);
	E3Result updateContent(TbContent tbContent,long categoryId,long id);
	E3Result addContent(TbContent tbContent,long categoryId);
	List<TbContent> getContentListByCid(long cid);
}
