package com.dong.maker.meta;

public enum ModelTypeEnum {

    STRING("字符串", "string"),
    BOOLEAN("布尔", "boolean");


    private final String text;

    private final String value;

    private ModelTypeEnum(String text, String value)
    {
        this.text = text;
        this.value = value;
    }

    public String getText()
    {
        return text;
    }

    public String getValue()
    {
        return value;
    }
}
