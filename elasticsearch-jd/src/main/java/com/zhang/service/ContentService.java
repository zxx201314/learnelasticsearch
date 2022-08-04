package com.zhang.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface ContentService {
    /**
     * 1、解析数据放入 es 索引中
     */
    Boolean parseContent(String keyword) throws IOException;

    /**
     * 2、根据keyword分页查询结果
     */
    List<Map<String, Object>> search(String keyword, Integer pageIndex, Integer pageSize) throws IOException;
}
