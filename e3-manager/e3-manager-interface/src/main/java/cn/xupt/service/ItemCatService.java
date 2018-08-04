package cn.xupt.service;

import java.util.List;

import cn.xupt.common.pojo.EasyUITreeNode;

public interface ItemCatService {

	List<EasyUITreeNode> getItemCatlist(long parentId);
}
