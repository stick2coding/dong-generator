package com.dong.maker.model;

import lombok.Data;

/**
 * 动态模板配置
 * 注意要给字段添加默认值，避免生成过程中没有赋值导致生成错误
 */
@Data
public class DataModel {
    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 核心模板
     */
    public MainTemplate mainTemplate = new MainTemplate();

    /**
     * 静态内部类标识分组
     */
    @Data
    public static class MainTemplate {
        /**
         * 作者名称
         */
        private String author = "sunbin";

        /**
         * 输出信息
         */
        private String outputText = "sum =";
    }
}


