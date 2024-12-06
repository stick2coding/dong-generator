package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.dong.generator.MainGenerator;
import com.dong.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "json-generate", description = "读取json文件生成代码", mixinStandardHelpOptions = true)
@Data
public class JsonGenerateCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-f", "--file"}, arity = "0..1", description = "JSON路径", interactive = true, echo = true)
    private String filePath;

    public Integer call() throws TemplateException, IOException {
    String jsonStr = FileUtil.readUtf8String(filePath);
    DataModel model = JSONUtil.toBean(jsonStr, DataModel.class);
    MainGenerator.doGenerate(model);
    return 0;
    }
    }
