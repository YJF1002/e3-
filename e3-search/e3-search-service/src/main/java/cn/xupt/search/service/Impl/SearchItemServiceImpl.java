package cn.xupt.search.service.Impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xupt.common.pojo.SearchItem;
import cn.xupt.common.utils.E3Result;
import cn.xupt.search.mapper.ItemMapper;
import cn.xupt.search.service.SearchItemService;
/**
 * 索引库维护
 * <p>Title: SearchItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class SearchItemServiceImpl implements SearchItemService{

	@Autowired
	private SolrServer solrServer;
	@Autowired
	private ItemMapper itemMapper;
	
	@Override
	public E3Result importAllItems() {
		//查询商品列表
		List<SearchItem> itemList =  itemMapper.getItemList();
		//遍历商品列表
		
			//把文档对象写入索引库
			try {
				for (SearchItem searchItem : itemList) {
					//创建文档对象
					SolrInputDocument document = new SolrInputDocument();
					//向文挡中添加域
					document.addField("id", searchItem.getId());
					document.addField("item_title", searchItem.getTitle());
					document.addField("item_sell_point", searchItem.getSell_point());
					document.addField("item_price", searchItem.getPrice());
					document.addField("item_image", searchItem.getImage());
					document.addField("item_category_name", searchItem.getCategory_name());
					solrServer.add(document);
				}	
				
				//提交
				solrServer.commit();
				//返回导入成功
				return E3Result.ok();
			} catch (Exception e) {
				e.printStackTrace();
				return E3Result.build(500,"数据导入时发生异常");
			
		}
		
	}

}
