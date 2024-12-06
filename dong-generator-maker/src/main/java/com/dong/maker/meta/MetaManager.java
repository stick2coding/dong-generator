package com.dong.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {

    private static volatile Meta meta;

    private MetaManager() {}

    public static Meta getMetaObject(){
        if (meta == null){
            synchronized (MetaManager.class){
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        System.out.println("metaJson: " + metaJson);
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        //Meta.FileConfig fileConfig = newMeta.getFileConfig();
        MetaValidator.doValidateAndFill(newMeta);
        return newMeta;
    }

}
