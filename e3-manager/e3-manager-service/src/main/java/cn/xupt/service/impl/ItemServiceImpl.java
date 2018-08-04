package cn.xupt.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.xml.internal.rngom.digested.DAnnotation;

import cn.xupt.common.jedis.JedisClient;
import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.common.utils.IDUtils;
import cn.xupt.common.utils.JsonUtils;
import cn.xupt.mapper.TbItemDescMapper;
import cn.xupt.mapper.TbItemMapper;
import cn.xupt.pojo.TbItem;
import cn.xupt.pojo.TbItemDesc;
import cn.xupt.pojo.TbItemExample;
import cn.xupt.pojo.TbItemExample.Criteria;
import cn.xupt.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private JedisClient jedisClient;
	@Resource
	private Destination topicDestination;
	
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if(StringUtils.isNoneBlank(json)){
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有查询数据库
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		//执行查询
		if(list != null && list.size() > 0){
			try {
				
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", ITEM_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);
		}
		return null;
		
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		//取分页结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		//取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}

	@Override
	public E3Result addItem(TbItem item,String desc) {
		//生成商品id
		final long itemId = IDUtils.genItemId();
		//补全item属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除'
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//商品表插入数据
		itemMapper.insert(item);
		//创建商品描述表pojo对象
		TbItemDesc itemDesc = new TbItemDesc();
		//补全属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//发送一个商品添加消息
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		//返回成功
		return E3Result.ok();
	}




	@Override
	public E3Result selectTbItemDesc(long id) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		 E3Result result = E3Result.ok(itemDesc);
		return result;
	}

	@Override
	public E3Result updateItem(long id,TbItem item, String desc) {
		//生成商品id
		item.setId(id);
		item.setUpdated(new Date());
		item.setStatus((byte) 1);
		item.setCreated(itemMapper.selectByPrimaryKey(id).getCreated());
		//商品表更新数据
		itemMapper.updateByPrimaryKey(item);
		
		//得到商品描述表pojo对象
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		//补全属性
		itemDesc.setItemId(id);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(itemDesc.getCreated());
		itemDesc.setUpdated(new Date());
		//向商品描述表更新数据
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		//返回成功
		return E3Result.ok();
			
	}

	@Override
	public E3Result dropoffItem(long[] itemId) {
		for (long l : itemId) {
			TbItem item = itemMapper.selectByPrimaryKey(l);
			item.setStatus((byte) 2);
			item.setCreated(item.getCreated());
			item.setUpdated(new Date());
			itemMapper.updateByPrimaryKeySelective(item);
		}
		return E3Result.ok();
	}

	@Override
	public E3Result upperoffItem(long[] itemId) {
		for (long l : itemId) {
			TbItem item = itemMapper.selectByPrimaryKey(l);
			item.setStatus((byte) 1);
			item.setCreated(item.getCreated());
			item.setUpdated(new Date());
			itemMapper.updateByPrimaryKeySelective(item);
		}
		return E3Result.ok();
	}
	@Override
	public E3Result deleteItem(long[] itemId) {
		for (long l : itemId) {
			itemMapper.deleteByPrimaryKey(l);
			itemDescMapper.deleteByPrimaryKey(l);
		}
		return E3Result.ok();
	}

	@Override
	public TbItemDesc geTbItemDescById(long itemId) {
		
		//查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if(StringUtils.isNoneBlank(json)){
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//执行查询
			try {
				
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(tbItemDesc));
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", ITEM_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return tbItemDesc;
	}

}
