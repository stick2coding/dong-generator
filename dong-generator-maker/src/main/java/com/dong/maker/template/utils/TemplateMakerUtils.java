package com.dong.maker.template.utils;

import cn.hutool.core.util.StrUtil;
import com.dong.maker.meta.Meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 */
public class TemplateMakerUtils {


    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList){
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>();

        //先筛选出所有的分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        //拿出所有的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupFileInfoList
                .stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());

        //获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());

        //移出所有名称在set内的外层文件（遍历时如果有分组就不存在inputPath，所以直接用原列表做过滤即可）
        resultList = fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());

        return resultList;

    }

}
