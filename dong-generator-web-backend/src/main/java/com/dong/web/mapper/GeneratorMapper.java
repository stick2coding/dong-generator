package com.dong.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dong.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author sunbin
* @description 针对表【generator(代码生成器)】的数据库操作Mapper
* @createDate 2024-11-28 16:38:16
* @Entity generator.domain.Generator
*/
public interface GeneratorMapper extends BaseMapper<Generator> {


    /**
     * 查找已删除的生成器
     * @return
     */
    @Select("select id, distPath from generator where isDelete = 1")
    List<Generator> listGeneratorWithDelete();

}




