package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态文件生成
 */
public class DynamicGenerator {
    /**
     * 抽取生成方法，将需要的输入路径和输出路径做为参数
     * @param inputPath
     * @param outputPath
     * @param mainTemplateConfig
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerate(String inputPath, String outputPath, Object mainTemplateConfig) throws IOException, TemplateException {
        //定义全局配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        //指定模板！配置文件集合！的路径
        configuration.setDirectoryForTemplateLoading(new File(inputPath).getParentFile());
        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("UTF-8");

        //创建爱你模板对象，加载指定的模板
        Template template = configuration.getTemplate(new File(inputPath).getName());

        //上级目录是否存在，若不存在则新建
        String parentPath = new File(outputPath).getParentFile().getAbsolutePath();
        System.out.println(parentPath);
        if (!FileUtil.exist(parentPath)){
            FileUtil.mkdir(parentPath);
        }
        //生成动态文件
        Writer out = new FileWriter(outputPath);
        template.process(mainTemplateConfig, out);

        //关闭文件流
        out.close();
    }
}
