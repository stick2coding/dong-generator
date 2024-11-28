package com.dong.maker.template.model;

import lombok.Data;


/**
 * ftl文件的输出配置
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 移出未分组目录下和分组内重名的文件配置
     */
    private boolean removerGroupFilesFromRoot = true;

}
