package com.dong.maker.cli.command;

import com.dong.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

@CommandLine.Command(name = "testGroup", mixinStandardHelpOptions = true)
public class TestGroupCommand implements Runnable{
    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环", arity = "0..1", interactive = true, echo = true)
    private boolean loop;

    static DataModel.MainTemplate mainTemplate = new DataModel.MainTemplate();

    @Override
    public void run() {
        System.out.println(loop);
        if(true){
            System.out.println("输入核心模板参数：");
            CommandLine commandLine = new CommandLine(MainTemplateCommand.class);
            commandLine.execute("-a", "-o");
            System.out.println(mainTemplate);
        }
        System.out.println("结束...");
    }

    @CommandLine.Command(name = "mainTemplate")
    @Data
    public static class MainTemplateCommand implements Runnable{

        @CommandLine.Option(names = {"-a", "--sauthor"}, description = "作者", arity = "0..1", interactive = true, echo = true)
        private String author = "dongdongTest";

        @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出文本", arity = "0..1", interactive = true, echo = true)
        private String outputText = "test result";
        @Override
        public void run() {
            mainTemplate.setAuthor(author);
            mainTemplate.setOutputText(outputText);
        }
    }



//    @Data
//    static class MainTemplate {
//        @CommandLine.Option(names = {"-mainTemplate.a", "--mainTemplate.author"}, description = "作者", arity = "0..1", interactive = true, echo = true)
//        private String author = "dongdongTest";
//
//        @CommandLine.Option(names = {"-mainTemplate.o", "--mainTemplate.outputText"}, description = "输出文本", arity = "0..1", interactive = true, echo = true)
//        private String outputText = "test result";
//    }

    public static void main(String[] args) {
//        CommandLine commandLine = new CommandLine(TestArgsGroupCommand.class);
        CommandLine commandLine = new CommandLine(TestGroupCommand.class);
        //commandLine.execute("-l", "-mainTemplate.a", "--mainTemplate.outputText");
//        commandLine.execute("--help");
        commandLine.execute("-l");
    }
}
