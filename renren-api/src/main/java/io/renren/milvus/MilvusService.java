package io.renren.milvus;

import com.google.common.collect.ImmutableList;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.GetIndexBuildProgressResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.GetIndexBuildProgressParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.renren.config.MilvusConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class MilvusService {
    @Autowired
    private MilvusConfig milvusConfig;
    //创建集合
    public boolean creatCollection(String collectionName){
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        FieldType pdfID = FieldType.newBuilder()
                .withName("PDF_ID")
                .withDescription("主键id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType pdfEmbde = FieldType.newBuilder()
                .withName("PDF_Embed")
                .withDescription("PDF_embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(1536)
                .build();
        FieldType pdfContent = FieldType.newBuilder()
                .withName("PDF_Content")
                .withDescription("PDF内容")
                .withDataType(DataType.VarChar)
                .withMaxLength(8192)
                .build();
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("PDF向量表"+collectionName)
                .withShardsNum(PushMaterielsConfig.SHARDS_NUM)
                .addFieldType(pdfID)
                .addFieldType(pdfEmbde)
                .addFieldType(pdfContent)
                .build();
        R<RpcStatus> response = milvusClient.createCollection(createCollectionReq);
        log.info(PushMaterielsConfig.COLLECTION_NAME+"是否成功创建集合——>>"+response.getStatus());

        return response.getStatus() == 0 ? true : false;
    }
    //判断集合是否已经存在
    public boolean isExitCollection(String collectionName){
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<Boolean> response = milvusClient.hasCollection(
                HasCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        return response.getStatus() == 0 ? true : false;
    }
    //createPartition(创建分区) 【可选，不创建则会选择默认分区进行数据存储】
    public void createPartition(String collectionName, String partitionName){
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.createPartition(CreatePartitionParam.newBuilder()
                .withCollectionName(collectionName) //集合名称
                .withPartitionName(partitionName) //分区名称
                .build());
    }

    /**
     * 先定义了分区总数PARTITION_NUM, 然后循环建立分区,在查询或者插入的时候根据里面的某个值进行取模,分到对应的分区里面去
     * PARTITION_NUM=10
     * */
    public void test(){
        for (int i = 0; i < 10; i++) {
            createPartition(PushMaterielsConfig.COLLECTION_NAME, PushMaterielsConfig.PARTITION_PREFIX + i);
        }
    }

    /**
     * 创建索引
     */
    public R<RpcStatus> createIndex(String collectionName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("PDF_Embed")
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.IP)
                //nlist 建议值为 4 × sqrt(n)，其中 n 指 segment 最多包含的 entity 条数。
                .withExtraParam("{\"nlist\":1024}")
                .withSyncMode(Boolean.TRUE)
                .build());
        log.info("createIndex-------------------->{}", response.toString());
        R<GetIndexBuildProgressResponse> idnexResp = milvusClient.getIndexBuildProgress(
                GetIndexBuildProgressParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        log.info("getIndexBuildProgress---------------------------->{}", idnexResp.toString());
        return response;
    }
    /**
     * loadCollection（加载集合，在插入数据前，如果集合未处于加载在内存中，则需要先加载集合和分区，分区加载为可选（如果是默认分区，则可以不加载分区））
     */
    /**
     * 加载集合
     * */
    public boolean loadCollection(String collectionName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                //集合名称
                .withCollectionName(collectionName)
                .build());
        log.info("loadCollection------------->{}", response);
        return response.getStatus() == 0 ? true : false;
    }

    /**
     * 加载分区
     * */
    public void loadPartitions(String collectionName, String partitionsName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.loadPartitions(
                LoadPartitionsParam
                        .newBuilder()
                        //集合名称
                        .withCollectionName(collectionName)
                        //需要加载的分区名称
                        .withPartitionNames(Arrays.asList(partitionsName))
                        .build()
        );
        log.info("loadCollection------------->{}", response);
    }
    /**
     * releaseCollection（从内存中释放集合）
     */
    /**
     * 从内存中释放集合
     * */
    public void releaseCollection(String collectionName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        log.info("releaseCollection------------->{}", response);
    }

    /**
     * 释放分区
     * */
    public void releasePartition(String collectionName, String partitionsName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .addPartitionName(partitionsName)
                .build());
        log.info("releasePartition------------->{}", response);
    }
    public boolean insert(String collectionName, List<String> strings, List<List<Float>> embeddings){
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("PDF_Content", strings));
        fields.add(new InsertParam.Field("PDF_Embed", embeddings));
        return insert(collectionName, fields);

    }
    //插入数据
    public boolean insert(String collectionName, List<InsertParam.Field> fields ){
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        //插入
        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                //.withPartitionName(partitionName)
                .withFields(fields)
                .build();
        R<MutationResult> insert = milvusClient.insert(insertParam);
        log.info("插入:{}", insert);
        return insert.getStatus() == 0 ? true : false;
    }

    public R<SearchResults> searchContent(List<List<Float>> vectors, String collectionName) {
        MilvusServiceClient milvusClient = milvusConfig.milvusServiceClient();
        final String SEARCH_PARAM = "{\"nprobe\":10}";    // Params
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(MetricType.IP)
                .withOutFields(ImmutableList.of("PDF_Content"))
                .withTopK(20)
                .withVectors(vectors)
                .withVectorFieldName("PDF_Embed")
                .withParams(SEARCH_PARAM)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        return response;
    }
}
