package com.dong.maker.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.dong.maker.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config", description = "查看参数配置", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{
    @Override
    public void run() {
        System.out.println("查看参数信息:");

        Class myClass = DataModel.class;
        Field[] fields = myClass.getDeclaredFields();

        Field[] fieldsByHutool = ReflectUtil.getFields(DataModel.class);

        for (Field field : fields){
            System.out.println("字段名称：" + field.getName());
            System.out.println("字段类型：" + field.getType());
            System.out.println("---");
        }

    }
}