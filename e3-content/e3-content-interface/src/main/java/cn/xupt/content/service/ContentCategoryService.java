package cn.xupt.content.service;

import java.util.List;

import cn.xupt.common.pojo.EasyUITreeNode;
import cn.xupt.common.utils.E3Result;

public interface ContentCategoryService {

	List<EasyUITreeNode> getContentCatList(long prentId);
	E3Result addContentCategory(Long parentId,String name);
	E3Result deleteContentCategory(Long id);
	E3Result updateContentCategory(Long id,String name);
}
