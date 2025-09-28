package io.renren.oss.cloud;

import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.auth.BceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectRequest;

/**
 * 百度云 BOS 存储实现
 *
 * @author
 */
public class BaiduCloudStorageService extends AbstractCloudStorageService {

    private BosClient client;

    public BaiduCloudStorageService(CloudStorageConfig config){
        this.config = config;

        BosClientConfiguration bosConfig = new BosClientConfiguration();
        bosConfig.setEndpoint(config.getBaiduEndPoint()); // 例："https://bj.bcebos.com"
        bosConfig.setCredentials(new DefaultBceCredentials(config.getBaiduAccessKeyId(), config.getBaiduSecretAccessKey()));
        this.client = new BosClient(bosConfig);
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            PutObjectRequest request = new PutObjectRequest(config.getBaiduBucketName(), path, inputStream);
            client.putObject(request);
        } catch (Exception e) {
            throw new RenException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }

        return config.getBaiduDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getBaiduPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getBaiduPrefix(), suffix));
    }
}
