package cn.xupt.search.mapper;

import java.util.List;

import cn.xupt.common.pojo.SearchItem;

public interface ItemMapper {

	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
