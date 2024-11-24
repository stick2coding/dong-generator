package com.dong.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();

        //模板文件输入路径
        String inputPath = new File(parentFile, "dong-generator.bat-demo-projects/acm-template").getAbsolutePath();
        List<File> fileList = FileUtil.loopFiles(inputPath);
        for (File file : fileList) {
            System.out.println(file.getAbsolutePath());
        }


    }
}
