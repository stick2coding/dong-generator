package com.dong.maker.generator.main;

import com.dong.maker.meta.Meta;

import java.io.IOException;

/**
 * 集成模板方法的生成器，默认可以使用模板方法
 * 如果和模板方法业务有差别，可以通过复写模板方法来实现个性化业务
 */
public class MainGeneratorNew extends GenerateTemplate{


    @Override
    protected void buildDist(String outputPath, String jarPath, String shellScriptOutputFilePath, String sourceCopyDestPath) {
        System.out.println("不需要再生成dist!");
    }
}
