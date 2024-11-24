package com.dong.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * 创建一个类，实现Runable或者Callable接口的类
 * @Command 标记该类为一个命令
 * mixinStandardHelpOptions = true 可以给命令自动添加--help和--version 选项
 *
 */
@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable {

    // option标记为一个命令选项
    @Option(names = { "-s", "--font-size" }, description = "Font size")
    int fontSize = 19;

    // parameters标记为一个参数
    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" };

    @Override
    public void run() {
        // 自己实现业务逻辑
        System.out.println("fontSize = " + fontSize);
        System.out.println("words = " + String.join(",", words));
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.exit(exitCode);
    }
}
