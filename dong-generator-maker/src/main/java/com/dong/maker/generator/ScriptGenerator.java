package com.dong.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ScriptGenerator {

    public static void main(String[] args) {
        String outputPath = System.getProperty("user.dir") + File.separator + "generator.bat";
        doGenerate(outputPath, "");
    }

    public static void doGenerate(String outputPath, String jarPath){
        StringBuilder scriptStrBuilder = new StringBuilder();

        scriptStrBuilder.append("@echo off").append("\n");
        scriptStrBuilder.append(String.format("java -jar %s %%*", jarPath)).append("\n");
        FileUtil.writeBytes(scriptStrBuilder.toString().getBytes(StandardCharsets.UTF_8), outputPath);
    }
}
