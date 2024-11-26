package com.dong.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TemplateMakerFileConfig {

    /**
     * 文件及文件过滤规则，具体见内部类
     */
    private List<FileInfoConfigV2> fileInfoConfigList;

    /**
     * 文件分组，用于将同一组文件放在一组元信息中。这样在生成代码时可以根据同一个参数来控制一组文件的生成
     */
    private FileGroupConfig fileGroupConfig;

    /**
     * 文件及过滤规则
     */
    @NoArgsConstructor
    @Data
    public static class FileInfoConfigV2 {

        private String filePath;

        private List<FileFilterConfig> fileFilterConfigList;
    }


    /**
     * 文件分组信息
     *
     */
    @Data
    public static class FileGroupConfig {

        private String groupName;

        private String groupKey;

        private String condition;
    }

}
