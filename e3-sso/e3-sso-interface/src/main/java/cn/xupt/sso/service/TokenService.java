package cn.xupt.sso.service;

import cn.xupt.common.utils.E3Result;

public interface TokenService {

	E3Result getUserByToken(String token);
}
