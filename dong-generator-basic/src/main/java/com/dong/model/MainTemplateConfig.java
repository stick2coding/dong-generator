package com.dong.model;

import lombok.Data;

/**
 * 动态模板配置
 * 注意要给字段添加默认值，避免生成过程中没有赋值导致生成错误
 */
@Data
public class MainTemplateConfig {
    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者名称
     */
    private String author = "sunbin";

    /**
     * 输出信息
     */
    private String outputText = "sum =";
}
