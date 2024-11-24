package com.dong.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class StaticGenerator {

    /**
     * 文件复制
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByHutool(String inputPath, String outputPath){
        FileUtil.copy(inputPath, outputPath, false);
    }

    public static void copyFilesByRecursive(String inputPath, String outputPath){
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try{
            copyFileByRecursive(inputFile, outputFile);
        }catch(Exception e) {
            System.err.println("文件复制失败");
        };

    }

    /**
     * 文件A 到 目录B 则文件放在目录下
     * 文件A 到 文件B 则覆盖
     * 目录A 到 目录B 则目录放在目录下
     * @param inputFile
     * @param outputFile
     */
    private static void copyFileByRecursive(File inputFile, File outputFile) throws IOException {
        //判断当前是文件还是目录
        if(inputFile.isDirectory()){
            System.out.println("目录：" + inputFile.getName());
            //判断输出目录是否存在，不存在则创建
            File destOutputFile = new File(outputFile, inputFile.getName());
            if(!destOutputFile.exists()){
                destOutputFile.mkdirs();
            }
            // 复制目录下的目录和文件
            File[] files = inputFile.listFiles();
            // 如果为空则返回
            if (ArrayUtil.isEmpty(files)){
                return;
            }
            // 不为空则递归复制，调用复制方法，依次将目录创建
            for (File file : files) {
                copyFileByRecursive(file, destOutputFile);
            }
        } else {
            // 如果是文件，则直接输出到对应的目录下即可
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            System.out.println(destPath.toString());
            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }

    }


    public static void main(String[] args) {
        //获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        System.out.println("当前项目路径：" + projectPath);
        File parentPath = new File(projectPath).getParentFile();
        //输入路径
        String inputPath = new File(parentPath, "dong-generator.bat-demo-projects/acm-template").getAbsolutePath();
        //输出路径
        String outputPath = projectPath;
        copyFilesByHutool(inputPath, outputPath);
    }

}
