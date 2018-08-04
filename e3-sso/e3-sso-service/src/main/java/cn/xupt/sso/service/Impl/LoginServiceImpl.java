package cn.xupt.sso.service.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.xupt.common.jedis.JedisClient;
import cn.xupt.common.utils.E3Result;
import cn.xupt.common.utils.JsonUtils;
import cn.xupt.mapper.TbUserMapper;
import cn.xupt.pojo.TbUser;
import cn.xupt.pojo.TbUserExample;
import cn.xupt.pojo.TbUserExample.Criteria;
import cn.xupt.sso.service.LoginService;
/**
 * 用户登录处理
 * <p>Title: LoginServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public E3Result userLogin(String username, String password) {
		
		 //1、判断用户名和密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0){
			//返回登录失败
			return E3Result.build(400, "用户名或密码错误");
		}
		//取用户信息
		TbUser user = list.get(0);
		//判断密码是否正确
		if(DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
			//2、如果不正确，返回登陆失败
			return E3Result.build(400, "用户名或密码错误");
		}
		 //3、如果正确生成token
		String token = UUID.randomUUID().toString();
		 //4、把用户信息写入redis。key：token value：用户信息
		user.setPassword(null);
		jedisClient.set("SESSION:" + token, JsonUtils.objectToJson(user));
		 //5、设置Session过期时间
		jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
		 //6、返回token
		
		//返回值：E3Result,其中包含token信息
		
		return E3Result.ok(token);
	}

}
