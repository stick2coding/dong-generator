package com.dong.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模板制作所需的模型配置
 */
@Data
public class TemplateMakerModelConfig {

    /**
     * 文件及文件过滤规则，具体见内部类
     */
    private List<ModelInfoConfig> modelInfoConfigList;

    /**
     * 文件分组，用于将同一组文件放在一组元信息中。这样在生成代码时可以根据同一个参数来控制一组文件的生成
     */
    private ModelGroupConfig modelGroupConfig;

    /**
     * 模型及具体配置（参考meta.json）
     */
    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {

        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;

        private String abbr;

        //要替换哪些文本
        private String replaceText;

    }


    /**
     * 模型分组信息
     *
     */
    @Data
    public static class ModelGroupConfig {

        private String groupName;

        private String groupKey;

        private String description;

        /**
         * 前置命令
         */
        private String condition;

        private String type;
    }

}
