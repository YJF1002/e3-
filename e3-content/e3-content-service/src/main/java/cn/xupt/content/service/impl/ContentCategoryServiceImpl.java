package cn.xupt.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.xupt.common.pojo.EasyUITreeNode;
import cn.xupt.common.utils.E3Result;
import cn.xupt.content.service.ContentCategoryService;
import cn.xupt.mapper.TbContentCategoryMapper;
import cn.xupt.pojo.TbContentCategory;
import cn.xupt.pojo.TbContentCategoryExample;
import cn.xupt.pojo.TbItem;
import cn.xupt.pojo.TbItemExample;
import cn.xupt.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类管理
 * <p>Title: ContentCategoryServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCatList(long prentId) {
		//根据prentid查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(prentId);
		//执行查询
		List<TbContentCategory>catList = contentCategoryMapper.selectByExample(example);
		//转换成
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		for(TbContentCategory tbContentCategory : catList){
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到列	
			nodeList.add(node);
		}
		
		return nodeList;
	}

	@Override
	public E3Result addContentCategory(Long parentId, String name) {
		//创建一个tb_content_category表对应的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//设置pojo属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//1正常2.删除
		//新添加的节点一定是叶子节点
		contentCategory.setStatus(1);
		contentCategory.setIsParent(false);
		contentCategory.setSortOrder(1);
		contentCategory.setCreated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的isparent属性，如果不是true改为true
		//根据parentid查询父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			parent.setIsParent(true);
			//更新到数据库中
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果，返回E3result
		return E3Result.ok(contentCategory);
	}

	@Override
	public E3Result deleteContentCategory(Long id) {
		deleteCategoryAndChildNode(id);
		return E3Result.ok();
	}

	@Override
	public E3Result updateContentCategory(Long id, String name) {
		//创建一个tb_content_category表对应的pojo对象
				TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id) ;
				//设置pojo属性
				contentCategory.setName(name);
				//更新到数据库
				contentCategoryMapper.updateByPrimaryKey(contentCategory);
				return E3Result.ok(contentCategory);
			}
	
	
	public List<TbContentCategory> getChildNodeList(Long id){
		//查询所有的子节点对象
		TbContentCategoryExample example = new TbContentCategoryExample();
		TbContentCategoryExample.Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		return contentCategoryMapper.selectByExample(example);
		
	}
	
	public void deleteCategoryAndChildNode(Long id){
		//获取category对象
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		//判断是否为父节点
		if(tbContentCategory.getIsParent()){
			List<TbContentCategory> list = getChildNodeList(id);
			//删除所有孩子节点
			for (TbContentCategory Category : list) {
				deleteCategoryAndChildNode(id);
			}
		}
		//判断父节点有没有其他子节点
		if (getChildNodeList(tbContentCategory.getParentId()).size()==1) {
			//没有则将父节点变为叶子节点
			TbContentCategory pContentCategory = contentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
			pContentCategory.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(pContentCategory);
			
		}
		contentCategoryMapper.deleteByPrimaryKey(id);
		return;
	}

	}


