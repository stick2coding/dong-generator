package com.dong.cli.example;

import org.apache.commons.collections4.sequence.DeleteCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "main", mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable{

    @Override
    public void run() {
        System.out.println("主命令!");
    }

    @Command(name ="add", description = "增加", mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable{

        @Override
        public void run() {
            System.out.println("AddCommand");
        }
    }

    @Command(name = "delete", description = "删除", mixinStandardHelpOptions = true)
    static class deleteCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("deleteCommand");
        }
    }

    @Command(name = "query", description = "查询", mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("queryCommand");
        }
    }

    public static void main(String[] args) {
        String[] myArgs = new String[] {};
        int exitCode = new CommandLine(new SubCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new deleteCommand())
                .addSubcommand(new QueryCommand())
                .execute(myArgs);
        System.exit(exitCode);
    }
}
