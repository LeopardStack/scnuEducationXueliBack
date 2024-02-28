package com.scnujxjy.backendpoint.service.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.model.bo.es.FileDocumentBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.text.Text;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElasticsearchService {
    private RestHighLevelClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.elasticsearch.index_name}")
    private String indexName;
    @Value("${spring.elasticsearch.pipelineName}")
    private String pipelineName;

    public ElasticsearchService(@Value("${spring.elasticsearch.uris}") String hostname,
                                @Value("${spring.elasticsearch.port}") int port,
                                @Value("${spring.elasticsearch.scheme}") String scheme) {
        this.client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(hostname, port, scheme)));
    }

    public void indexDocument(String index, String id, Map<String, Object> document) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index)
                .id(id)
                .source(document);
        client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void indexDocument(String id, Map<String, Object> document) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName)
                .id(id)
                .source(document);
        client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<Map<String, Object>> search(String index, String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("content", keyword));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return Arrays.stream(searchResponse.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> search(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("attachment.content", keyword));

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("attachment.content");
        highlightBuilder.field(highlightContent);
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return Arrays.stream(searchResponse.getHits().getHits())
                .map(hit -> {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    // 提取高亮结果
                    if (hit.getHighlightFields().get("attachment.content") != null) {
                        Text[] fragments = hit.getHighlightFields().get("attachment.content").fragments();
                        String highlightedText = String.join("", Arrays.stream(fragments)
                                .map(Text::string)
                                .collect(Collectors.toList()));
                        sourceAsMap.put("highlighted", highlightedText);
                    }
                    return sourceAsMap;
                })
                .collect(Collectors.toList());
    }

    public List<FileDocumentBO> accurateSearch(String keyword) throws IOException {
        List<FileDocumentBO> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 使用 match_phrase 查询进行精确短语匹配
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("attachment.content", keyword));

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("attachment.content");
        highlightBuilder.field(highlightContent);
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            try {
                FileDocumentBO document = mapToObject(hit.getSourceAsMap(), FileDocumentBO.class);
                if (hit.getHighlightFields().get("attachment.content") != null) {
                    Text[] fragments = hit.getHighlightFields().get("attachment.content").fragments();
                    String highlightedText = String.join("", Arrays.stream(fragments)
                            .map(Text::string)
                            .collect(Collectors.toList()));
                    document.setHighlightedContent(highlightedText);
                }
                results.add(document);
            } catch (Exception e) {
                // Handle exception
            }
        }
        return results;
    }

    public List<FileDocumentBO> accurateSearchWithoutFileRawData(String keyword) throws IOException {
        List<FileDocumentBO> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 创建布尔查询以同时搜索 fileName 和 attachment.content
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery("fileName", keyword));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("attachment.content", keyword));
        searchSourceBuilder.query(boolQueryBuilder);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("attachment.content");
        highlightBuilder.field(highlightContent);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 排除 data 字段
        searchSourceBuilder.fetchSource(null, new String[]{"data", "attachment.content"});

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            try {
                FileDocumentBO document = mapToObject(hit.getSourceAsMap(), FileDocumentBO.class);
                if (hit.getHighlightFields().get("attachment.content") != null) {
                    Text[] fragments = hit.getHighlightFields().get("attachment.content").fragments();
                    String highlightedText = String.join("", Arrays.stream(fragments)
                            .map(Text::string)
                            .collect(Collectors.toList()));
                    document.setHighlightedContent(highlightedText);
                }
                results.add(document);
            } catch (Exception e) {
                // Handle exception
            }
        }
        return results;
    }

    public List<FileDocumentBO> accurateSearchWithoutFileRawData(String keyword, String fileType) throws IOException {
        List<FileDocumentBO> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 创建布尔查询以同时搜索 fileName、attachment.content 和匹配文件类型
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery("fileName", keyword));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("attachment.content", keyword));
        if (fileType != null && !fileType.isEmpty()) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("type.keyword", fileType));
            boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("fileName", fileType));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("attachment.content");
        highlightBuilder.field(highlightContent);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 排除 data 和 attachment.content 字段
        searchSourceBuilder.fetchSource(null, new String[]{"data", "attachment.content"});

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            try {
                FileDocumentBO document = mapToObject(hit.getSourceAsMap(), FileDocumentBO.class);
                if (hit.getHighlightFields().get("attachment.content") != null) {
                    Text[] fragments = hit.getHighlightFields().get("attachment.content").fragments();
                    String highlightedText = String.join("", Arrays.stream(fragments)
                            .map(Text::string)
                            .collect(Collectors.toList()));
                    document.setHighlightedContent(highlightedText);
                }
                results.add(document);
            } catch (Exception e) {
                log.info("解析 ES 返回值失败 " + e.toString());
            }
        }
        return results;
    }



    public boolean createIndexWithIKAnalyzer(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        // 设置 IK 分词器
        request.settings(Settings.builder()
                .put("index.analysis.analyzer.default.type", "ik_smart"));

        // 创建索引
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    public boolean createIngestPipeline(String pipelineName) throws IOException {
        PutPipelineRequest pipelineRequest = new PutPipelineRequest(
                pipelineName,
                new BytesArray("{\n" +
                        "  \"description\": \"Extract attachment information\",\n" +
                        "  \"processors\": [\n" +
                        "    {\n" +
                        "      \"attachment\": {\n" +
                        "        \"field\": \"data\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"),
                XContentType.JSON
        );

        AcknowledgedResponse pipelineResponse = client.ingest().putPipeline(pipelineRequest, RequestOptions.DEFAULT);
        return pipelineResponse.isAcknowledged();
    }



    public void indexFile(String fileName, InputStream fileStream) throws IOException {
        // 将文件内容转为 Base64 编码
        byte[] bytes = readBytesFromStream(fileStream);
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        // 获取文件类型
        String fileType = fileName.contains(".") ?
                fileName.substring(fileName.lastIndexOf(".") + 1) : "unknown";

        // 创建文档
        Map<String, Object> document = new HashMap<>();
        document.put("fileName", fileName);
        document.put("fileType", fileType);
        document.put("uploadTime", new Date());
        document.put("data", base64Content); // "data" 是 ingest pipeline 处理的字段

        // 创建索引请求，并指定 pipeline
        IndexRequest indexRequest = new IndexRequest(indexName)
                .id(fileName)
                .source(document)
                .setPipeline(pipelineName); // 使用指定的 pipeline

        // 索引文档
        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error indexing file: {}", fileName, e);
            throw e; // 重新抛出异常或进行其他错误处理
        }
    }

    public void indexFile(FileDocumentBO fileDocument, InputStream fileStream) throws IOException {
        // 将文件内容转为 Base64 编码
        byte[] bytes = readBytesFromStream(fileStream);
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        // 设置文件内容
        fileDocument.setData(base64Content);

        // 使用 Jackson 序列化 FileDocumentBO 对象
        String jsonDocument = objectMapper.writeValueAsString(fileDocument);

        // 创建索引请求，并指定 pipeline
        IndexRequest indexRequest = new IndexRequest(indexName)
                .id(fileDocument.getId())
                .source(jsonDocument, XContentType.JSON)
                .setPipeline(pipelineName); // 使用指定的 pipeline

        // 索引文档
        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error indexing file: {}", fileDocument.getFileName(), e);
            throw e;
        }
    }



    public byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = map.get(field.getName());
            if (value != null) {
                field.set(obj, value);
            }
        }
        return obj;
    }


    /**
     * 删除索引 即将索引下的所有文件全部删除
     * @return
     * @throws IOException
     */
    public long deleteAllDocumentsInIndex() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(QueryBuilders.matchAllQuery()); // 匹配所有文档
        request.setRefresh(true); // 确保删除操作立即生效

        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        return response.getDeleted(); // 返回删除的文档数量
    }

}
