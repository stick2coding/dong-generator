package com.dong.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.dong.maker.template.enums.FileFilterRangeEnum;
import com.dong.maker.template.enums.FileFilterRuleEnum;
import com.dong.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件过滤
 */
public class FileFilter {

    /**
     * 主方法
     * 通过传入的路径（可以是目录）和规则，这里应该是绝对路径
     * 遍历所有文件，对文件进行规则过滤，并将符合条件的文件返回
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList){
        //获取当前路径下所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList, file))
                .collect(Collectors.toList());
    }

    /**
     * 通过传入文件和文件过滤规则来判断是否保留文件
     * @param fileFilterConfigList
     * @param file
     * @return
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        //过滤器结果
        boolean result = true;

        //如果过滤规则为空，直接返回
        if (CollUtil.isEmpty(fileFilterConfigList)){
            return result;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList){
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum rangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (rangeEnum == null){
                //如果当前过滤枚举找不到，就跳过
                continue;
            }

            //默认范围为文件名
            String content = fileName;
            switch (rangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum ruleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (ruleEnum == null){
                //如果当前过滤枚举找不到，就跳过
                continue;
            }
            switch(ruleEnum) {
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }

            //如果result出现false，表示有规则不符合，就返回
            if (!result){
                return result;
            }

        }

        //完全通过所有规则
        return result;
    }
}
