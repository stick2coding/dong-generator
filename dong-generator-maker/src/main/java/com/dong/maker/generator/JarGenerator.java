package com.dong.maker.generator;

import java.io.*;

public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String linuxMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        //读取命令输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        //等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("Maven打包完成，退出码：" + exitCode);
    }

    /**
     * 使用Java内置命令进行Maven打包
     * @param args
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("D:\\codingDevFile\\JavaLocalCode\\dong-generator\\dong-generator-maker\\generated\\acm-template-pro-generator");
    }
}
