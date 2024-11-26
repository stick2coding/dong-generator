package com.dong.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * 每个文件可能有多个规则
 */
@Data
public class FileInfoConfig {

    /**
     * 具体的文件路径
     */
    private String filePath;

    /**
     * 要执行的规则列表
     */
    private List<FileFilterConfig> filterConfigList;
}
