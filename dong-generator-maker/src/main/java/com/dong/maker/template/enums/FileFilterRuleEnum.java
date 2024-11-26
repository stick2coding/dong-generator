package com.dong.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 文件过滤规则枚举
 */
@Getter
public enum FileFilterRuleEnum {

    CONTAINS("包含", "contains"),
    STARTS_WITH("以...开头", "startsWith"),
    ENDS_WITH("以...结尾", "endsWith"),
    REGEX("正则匹配", "regex"),
    EQUALS("等于", "equals");

    private final String text;

    private final String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)){
            return null;
        }
        for (FileFilterRuleEnum anEnum : FileFilterRuleEnum.values()){
            if (anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }

}
