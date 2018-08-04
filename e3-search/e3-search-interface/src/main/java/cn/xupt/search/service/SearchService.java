package cn.xupt.search.service;

import cn.xupt.common.pojo.SearchResult;

public interface SearchService {

	SearchResult search(String keyword,int page,int rows) throws Exception;
}
