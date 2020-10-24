package com.ego.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {

    /**
     * 实现solr数据查询
     * @param q
     * @return
     */
    Map<String,Object> search(String q,int page,int size);

    /**
     * 对Solr中数据新增
     * @param ids 商品id集合
     * @return 新增结果
     */
    int insert(long []  ids);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    int delete(String [] ids);
}
