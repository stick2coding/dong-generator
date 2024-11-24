package com.dong.maker.cli;

import com.dong.maker.cli.command.ConfigCommand;
import com.dong.maker.cli.command.GenerateCommand;
import com.dong.maker.cli.command.ListCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "dong", mixinStandardHelpOptions =  true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        //不输入命令时，给出提示
        System.out.println("请输入具体的命令或者输入 --help 获取提示");

    }

    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
