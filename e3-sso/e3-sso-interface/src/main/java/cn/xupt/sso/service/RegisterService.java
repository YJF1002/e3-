package cn.xupt.sso.service;

import cn.xupt.common.utils.E3Result;
import cn.xupt.pojo.TbUser;

public interface RegisterService {

	E3Result checkData(String param, int type);
	E3Result register(TbUser user);
	
}
