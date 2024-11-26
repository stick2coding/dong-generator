package com.dong.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 用来描述文件的过滤规则
 */
@Data
public class FileFilterConfig {
    /**
     * 要过滤的范围（有文件名，文件内容）
     */
    private String range;

    /**
     * 过滤规则（等于，包含，以什么开始，以什么结束等）
     */
    private String rule;

    /**
     * 过滤的值
     */
    private String value;
}
