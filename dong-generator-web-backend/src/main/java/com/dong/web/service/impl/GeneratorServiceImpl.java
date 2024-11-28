package com.dong.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Generator;
import generator.mapper.GeneratorMapper;
import generator.service.GeneratorService;
import org.springframework.stereotype.Service;

/**
* @author sunbin
* @description 针对表【generator(代码生成器)】的数据库操作Service实现
* @createDate 2024-11-28 16:38:16
*/
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
    implements GeneratorService{

}




