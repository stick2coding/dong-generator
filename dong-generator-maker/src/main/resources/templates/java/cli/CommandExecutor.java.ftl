package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "${name}", mixinStandardHelpOptions =  true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new JsonGenerateCommand());
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
