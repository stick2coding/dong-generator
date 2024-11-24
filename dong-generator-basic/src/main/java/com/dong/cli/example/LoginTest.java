package com.dong.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class LoginTest implements Callable<Integer> {

    @Option(names = {"-u", "--user"}, description = "user name")
    String user;

    /**
     * interactive标识支持交互式输入
     * 默认情况下，命令中无法直接给交互式输入的选项直接输入参数，必须要通过命令行输入
     * 但是可以通过 arity 参数来支持在命令中直接填入交互式参数
     * arity="0..1"来表示可以支持直接输入，也支持命令中交互式输入
     */
    @Option(names = {"-p", "-password"}, description = "password", interactive = true, arity = "0..1")
    String password;

    @Option(names = {"-cp", "-checkPassword"}, description = "check password", interactive = true)
    String checkPassword;

    @Override
    public Integer call() throws Exception {
        System.out.println("password = " + password);
        System.out.println("check password = " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        args = new String[]{"-u", "sunbin", "-cp"};
        //检查是否包含全部的交互式选项命令，如果没有的话就自动补全
        args = checkOptions(args);

        //执行
        new CommandLine(new LoginTest()).execute(args);
    }

    /**
     * 加入命令中没有对应交互式参数的选项，则系统默认为NULL
     */
    private static String[] checkOptions(String[] args) {
        //将args参数转为list形式
        List<String> argList = new ArrayList<String>();
        for (String arg : args) {
            argList.add(arg);
        }
        //获取类中所有交互式命令
        List<String> interactiveOptionlist = new ArrayList<>();
        Class clazz = LoginTest.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取注解，筛选是option的
            if (field.isAnnotationPresent(Option.class)) {
                System.out.println(field.getName());
                Option option = field.getAnnotation(Option.class);
                if (option.interactive()){
                    interactiveOptionlist.add(option.names()[0]);
                }
            }
        }
        //判断所有交互式参数是否都在参数中，如果不在，则自动添加进参数中
        for (String interactiveOption : interactiveOptionlist) {
            if(!argList.contains(interactiveOption)){
                argList.add(interactiveOption);
            }
        }
        System.out.println(argList);
        return argList.toArray(new String[argList.size()]);
    }
}
