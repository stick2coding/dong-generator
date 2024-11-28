package com.dong.web.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        private String inputRootPath;
        private String outputRootPath;
        private String sourceRootPath;
        private String type;
        private List<FileInfo> files;

        @NoArgsConstructor
        @Data
        public static class FileInfo {
            private String groupKey;
            private String groupName;
            private String condition;
            private String type;
            private List<FileInfo> files;
            private String inputPath;
            private String outputPath;
            private String generateType;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<ModelInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelInfo {
            private String groupKey;
            private String groupName;
            private List<ModelInfo> models;
            private String fieldName;
            private String type;
            private String condition;
            private String description;
            private Object defaultValue;
            private String abbr;

            //如果存在groupKey，就需要增加一个参数字符串，用于调用下一级命令
            private String allArgsStr;
        }
    }
}
