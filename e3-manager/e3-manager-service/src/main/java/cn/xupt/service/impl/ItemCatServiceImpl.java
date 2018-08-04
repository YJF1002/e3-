package cn.xupt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;

import cn.xupt.common.pojo.EasyUITreeNode;
import cn.xupt.mapper.TbItemCatMapper;
import cn.xupt.pojo.TbItemCat;
import cn.xupt.pojo.TbItemCatExample;
import cn.xupt.pojo.TbItemCatExample.Criteria;
import cn.xupt.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Override
	public List<EasyUITreeNode> getItemCatlist(long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		//创建返回结果list
		List<EasyUITreeNode> resultList = new ArrayList<>();
		// 把列表转换为EasyUITreeNode列表
		for(TbItemCat tbItemCat : list){
			EasyUITreeNode node = new EasyUITreeNode();
			//设置属性
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			//添加到结果列表
			resultList.add(node);
		}
		return resultList;
	}

}
