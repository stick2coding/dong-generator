package com.dong.web.model.dto.generator;

import com.dong.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 使用请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class GeneratorUseRequest implements Serializable {

    /**
     * 生成器ID
     */
    private String id;


    /**
     * 数据模型
     */
    private Map<String, Object> dataModel;

    private static final long serialVersionUID = 1L;
}