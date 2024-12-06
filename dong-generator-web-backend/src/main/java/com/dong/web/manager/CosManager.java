package com.dong.web.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.dong.web.config.CosClientConfig;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 *
 * @author sunbin
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    // 复用这个对象
    private TransferManager transferManager;

    // bean加载完成后执行
    @PostConstruct
    public void init() {
        //线程池
        System.out.println("bean initialized");
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        transferManager = new TransferManager(cosClient, threadPool);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     * @param key 唯一key
     * @return
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    public Download download(String key, String localFilePath) throws InterruptedException {
        // 先在路径打开文件
        File downloadFile = new File(localFilePath);
        // 创建cos请求对象
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        // 下载
        Download download = transferManager.download(getObjectRequest, downloadFile);
        //等待
        download.waitForCompletion();
        return download;
    }


}
