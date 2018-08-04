package cn.xupt.service;



import java.util.List;

import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.pojo.TbItem;
import cn.xupt.pojo.TbItemDesc;

public interface ItemService {
	
	TbItem getItemById(long itemId);
	EasyUIDataGridResult getItemList(int page, int rows);
	E3Result addItem(TbItem item,String desc);
	E3Result deleteItem(long[] itemId);
	E3Result selectTbItemDesc(long id);
	E3Result updateItem(long id,TbItem item,String desc);
    E3Result dropoffItem(long[] itemId);
    E3Result upperoffItem(long[] itemId);
    TbItemDesc geTbItemDescById(long itemId);
	
}
