package com.zhang;

import com.alibaba.fastjson.JSON;
import com.zhang.dto.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class ElasticSearchApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void createIndex() {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("zhang_index");
        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            log.info(String.valueOf(createIndexResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    void judgeIndexExist() {
        GetIndexRequest getIndexRequest = new GetIndexRequest("zhang_index");
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        log.info(String.valueOf(exists));
    }

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("zhang_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        log.info(String.valueOf(delete.isAcknowledged()));
    }

    @Test
    void addDocument() {
        User user = new User("zhangxx", 28, true);
        IndexRequest indexRequest = new IndexRequest("zhang_index");
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueMinutes(2));
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        try {
            IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info(String.valueOf(index.status()));
            log.info(String.valueOf(index));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getDocument() {
        GetRequest getRequest = new GetRequest("zhang_index", "1");
        try {
            GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            log.info(documentFields.getSourceAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateDocument() {
        UpdateRequest updateRequest = new UpdateRequest("zhang_index", "1");
        User user = new User("zhangxx123", 29, true);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        try {
            UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info(String.valueOf(update.status()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    void deleteDocument() {
        DeleteRequest deleteRequest = new DeleteRequest("zhang_index", "1");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        log.info(delete.status().toString());
    }

    @Test
    void batchAddDocument() {
        IndexRequest indexRequest = new IndexRequest("zhang_index");
        BulkRequest bulkRequest = new BulkRequest();

        User user = new User("zhangxx0", 1, true);
        User user1 = new User("zhangxx1", 2, true);
        User user2 = new User("zhangxx2", 3, true);
        User user3 = new User("zhangxx3", 4, true);
        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        for (User u : list) {
            //无法实现批量新增，需要使用bulkRequest
//            indexRequest.source(JSON.toJSONString(u),XContentType.JSON);
            bulkRequest.add(new IndexRequest("zhang_index").source(JSON.toJSONString(u), XContentType.JSON));
        }
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 查询
    // SearchRequest 搜索请求
    // SearchSourceBuilder 条件构造
    // HighlightBuilder 高亮
    // TermQueryBuilder 精确查询
    // MatchAllQueryBuilder
    // xxxQueryBuilder ...
    @Test
    void searchDocument(){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //(1)查询条件 使用QueryBuilders工具类创建
        // 精确查询
        TermQueryBuilder termQueryBuilder =  QueryBuilders.termQuery("name","zhangxx3");
        // 匹配查询
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("age","2");
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("age","2");
        // (3)条件投入
        searchSourceBuilder.query(matchPhraseQueryBuilder);
        // 3.添加条件到请求
        searchRequest.source(searchSourceBuilder);
        // 4.客户端查询请求
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info(JSON.toJSONString(search.getHits().getHits()));
            for (SearchHit documentFields : search.getHits().getHits()) {
                System.out.println(documentFields.getSourceAsMap());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
