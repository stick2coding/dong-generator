package com.dong.maker.template.model;

import com.dong.maker.meta.Meta;
import lombok.Data;

/**
 *  模板制作的所有相关配置及输入参数
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta;

    private String originProjectPath;

    TemplateMakerModelConfig templateMakerModelConfig;

    TemplateMakerFileConfig templateMakerFileConfig;


}
