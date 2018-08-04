package cn.xupt.sso.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.xupt.common.jedis.JedisClient;
import cn.xupt.common.utils.E3Result;
import cn.xupt.common.utils.JsonUtils;
import cn.xupt.pojo.TbUser;
import cn.xupt.sso.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService{

	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public E3Result getUserByToken(String token) {
		//根据token到redis中取用户信息
		String json = jedisClient.get("SESSION:" + token);
		//取不到用户信息，登陆已经过期，返回登录过期
		if(StringUtils.isBlank(json)){
			return E3Result.build(201, "用户登陆已经过期");
		}
		//更新token的过期时间
		jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		//返回结果，E3Result其中包含TbUser对象
		return E3Result.ok(user);
	}

}
