package com.dong.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.dong.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config", description = "查看参数配置", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{
    @Override
    public void run() {
        System.out.println("查看参数信息:");

        Class myClass = MainTemplateConfig.class;
        Field[] fields = myClass.getDeclaredFields();

        Field[] fieldsByHutool = ReflectUtil.getFields(MainTemplateConfig.class);

        for (Field field : fields){
            System.out.println("字段名称：" + field.getName());
            System.out.println("字段类型：" + field.getType());
            System.out.println("---");
        }

    }
}
