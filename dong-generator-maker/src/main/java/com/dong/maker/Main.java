package com.dong.maker;

import com.dong.maker.cli.CommandExecutor;
import com.dong.maker.generator.main.MainGeneratorNew;
import com.dong.maker.generator.main.ZipGeneratorNew;
import freemarker.template.TemplateException;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        args = new String[]{"generate", "-l", "-a", "-o"};
        //args = new String[]{"config"};
        //args = new String[]{"list"};
//        CommandExecutor executor = new CommandExecutor();
//        executor.doExecute(args);

        // 每次新工程需要修改initMeta方法中的json文件路径后再执行
//        MainGeneratorNew mainGeneratorNew = new MainGeneratorNew();
//        mainGeneratorNew.doGenerate();

        ZipGeneratorNew zipGeneratorNew = new ZipGeneratorNew();
        zipGeneratorNew.doGenerate();

    }
}