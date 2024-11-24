import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {

        //创建模板配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        //指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        //指定字符集
        configuration.setDefaultEncoding("UTF-8");

        //加载模板
        Template template = configuration.getTemplate("myweb.html.ftl");

        //设置需要替换的数据
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2024);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> meun1 = new HashMap<>();
        meun1.put("url","www.baidu.com");
        meun1.put("label", "百度");
        menuItems.add(meun1);
        dataModel.put("menuItems", menuItems);

        //指定生成的文件
        Writer out = new FileWriter("myweb.html");

        //生成文件
        template.process(dataModel, out);

        //关闭流
        out.close();

    }




}
