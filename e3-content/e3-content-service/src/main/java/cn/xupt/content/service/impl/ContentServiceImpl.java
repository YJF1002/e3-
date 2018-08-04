package cn.xupt.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.xupt.common.jedis.JedisClient;
import cn.xupt.common.pojo.EasyUIDataGridResult;
import cn.xupt.common.utils.E3Result;
import cn.xupt.common.utils.IDUtils;
import cn.xupt.common.utils.JsonUtils;
import cn.xupt.content.service.ContentService;
import cn.xupt.mapper.TbContentMapper;
import cn.xupt.pojo.TbContent;
import cn.xupt.pojo.TbContentCategory;
import cn.xupt.pojo.TbContentExample;
import cn.xupt.pojo.TbContentExample.Criteria;


@Service
public class ContentServiceImpl implements ContentService{

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("CONTENT_LIST")
	private String CONTENT_LIST;
	
	
	@Override
	public EasyUIDataGridResult getContentList(long categoryId,int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria. andCategoryIdEqualTo(categoryId);
		//返回一个集合
		List<TbContent> list = contentMapper.selectByExample(example);	
		//创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		//取分页结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}
	
	
	@Override
	public E3Result deleteContent(long[] ids) {
		for (long l : ids) {
			contentMapper.deleteByPrimaryKey(l);
			jedisClient.hdel("CONTENT_LIST", contentMapper.selectByPrimaryKey(l).getCategoryId().toString());
		}
		
		return E3Result.ok(); 
	}
	
	
	@Override
	public E3Result updateContent(TbContent tbContent, long categoryId,long id) {
		//生成商品id
		tbContent.setCreated(contentMapper.selectByPrimaryKey(id).getCreated());
		tbContent.setUpdated(new Date());
		//内容表插入数据
		contentMapper.updateByPrimaryKey(tbContent);
		jedisClient.hdel("CONTENT_LIST", "categoryId");
		//返回成功
		return E3Result.ok();
	}
	@Override
	public E3Result addContent(TbContent tbContent, long categoryId) {
		//生成商品id
		long contentId = IDUtils.genItemId();
		//补全contentId属性
		tbContent.setCategoryId(categoryId);
		tbContent.setId(contentId);
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		//内容表插入数据
		contentMapper.insert(tbContent);
		//缓存同步，删除缓存中的数据
		jedisClient.hdel("CONTENT_LIST", "categoryId");
		//返回成功
		return E3Result.ok();
	}
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		//查询缓存
		try {
			//如果缓存中有直接响应结果
			String json = jedisClient.hget(CONTENT_LIST, cid + "");
			if(StringUtils.isNoneBlank(json)){
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//如果没有查询数据库
		//执行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria. andCategoryIdEqualTo(cid);
		//返回一个集合
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);	
		try {
			jedisClient.hset("CONTENT_LIST", cid + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
	}
}
