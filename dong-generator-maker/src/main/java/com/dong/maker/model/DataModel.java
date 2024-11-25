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
    private MainTemplate mainTemplate;
}
