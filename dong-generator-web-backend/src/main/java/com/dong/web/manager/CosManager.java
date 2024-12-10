package com.dong.web.manager;

import cn.hutool.core.collection.CollUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.dong.web.config.CosClientConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        System.out.println("cos manager bean initialized");
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

    /**
     * 下载到本地
     * @param key
     * @param localFilePath
     * @return
     * @throws InterruptedException
     */
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

    /**
     * 删除对象
     * @param key
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    /**
     * 批量删除对象
     * @param keyList
     * @return
     */
    public DeleteObjectsResult deleteObjects(List<String> keyList){
        // new一个批量删除请求
        DeleteObjectsRequest deletaObjectsRequest = new DeleteObjectsRequest(cosClientConfig.getBucket());
        ArrayList<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>();
        // 将要删除的key添加到这个keyversions中
        // key不能以正斜线或反斜线开头
        for (String key : keyList) {
            keyVersions.add(new DeleteObjectsRequest.KeyVersion(key));
        }
        deletaObjectsRequest.setKeys(keyVersions);
        //开始删除
        DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deletaObjectsRequest);
        return deleteObjectsResult;
    }

    public void deleteDir(String delPrefix){
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        //设置桶名
        listObjectsRequest.setBucketName(cosClientConfig.getBucket());
        //列出所有对象吗，prefix请以/结尾，避免错删文件
        listObjectsRequest.setPrefix(delPrefix);
        //设置遍历的最大数
        listObjectsRequest.setMaxKeys(1000);

        ObjectListing objectListing = null;

        do{
            //先列出文件
            objectListing = cosClient.listObjects(listObjectsRequest);
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            if (CollUtil.isEmpty(cosObjectSummaries)){
                break;
            }

            //新建一个列表存储待删除的key
            ArrayList<DeleteObjectsRequest.KeyVersion> deleteObjects = new ArrayList<>();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                deleteObjects.add(new DeleteObjectsRequest.KeyVersion(cosObjectSummary.getKey()));
            }

            //创建删除请求
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(cosClientConfig.getBucket());
            deleteObjectsRequest.setKeys(deleteObjects);
            //执行删除
            cosClient.deleteObjects(deleteObjectsRequest);

            //如果没有删除完，拿到下一次的位置
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);

        }while (objectListing.isTruncated());

    }


}
