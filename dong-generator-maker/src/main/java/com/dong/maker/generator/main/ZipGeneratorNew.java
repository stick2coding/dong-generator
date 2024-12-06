package com.dong.maker.generator.main;

/**
 * 集成模板方法的生成器，默认可以使用模板方法
 * 如果和模板方法业务有差别，可以通过复写模板方法来实现个性化业务
 */
public class ZipGeneratorNew extends GenerateTemplate{

//    @Override
//    protected String buildJar(Meta meta, String outputPath) {
//        System.out.println("不需要再生成jar!");
//        return null;
//    }


    @Override
    protected String buildDist(String outputPath, String jarPath, String shellScriptOutputFilePath, String sourceCopyDestPath) {
        String distPath = super.buildDist(outputPath, jarPath, shellScriptOutputFilePath, sourceCopyDestPath);
        return super.buildZip(distPath);
    }
}
