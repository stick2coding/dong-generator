package com.dong.web.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dong.maker.generator.main.GenerateTemplate;
import com.dong.maker.generator.main.ZipGeneratorNew;
import com.dong.maker.meta.Meta;
import com.dong.maker.meta.MetaValidator;
import com.dong.web.annotation.AuthCheck;
import com.dong.web.common.BaseResponse;
import com.dong.web.common.DeleteRequest;
import com.dong.web.common.ErrorCode;
import com.dong.web.common.ResultUtils;
import com.dong.web.config.GeneratorConfig;
import com.dong.web.constant.UserConstant;
import com.dong.web.exception.BusinessException;
import com.dong.web.exception.ThrowUtils;
import com.dong.web.manager.CacheManager;
import com.dong.web.manager.CosManager;
import com.dong.web.model.dto.generator.*;
import com.dong.web.model.entity.Generator;
import com.dong.web.model.entity.User;
import com.dong.web.model.vo.GeneratorVO;
import com.dong.web.service.GeneratorService;
import com.dong.web.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子接口
 *
 * @author sunbin
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorConfig generatorConfig;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    CacheManager cacheManager;

    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        Meta meta = generatorMakeRequest.getMeta();
        // 拿到路径
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        if (StrUtil.isBlank(zipFilePath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "路径不存在！");
        }

        //定义一个空间来存储临时文件
        String projectPath = System.getProperty("user.dir");
        // 随机ID
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        //创建临时空间
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        //新建文件来保存下载的内容
        String localZipPath = tempDirPath + "/project.zip";
        if (!FileUtil.exist(localZipPath)){
            FileUtil.touch(localZipPath);
        }
        // 下载
        try {
            cosManager.download(zipFilePath, localZipPath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败！");
        }
        // 解压后目录
        File unzipDistDir = ZipUtil.unzip(localZipPath);

        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        //校验
        MetaValidator.doValidateAndFill(meta);
        System.out.println("校验后的meta文件：" + meta);

        //指定输出路径
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());

        // 调用maker方法生成
        GenerateTemplate generateTemplate = new ZipGeneratorNew();
        try {
            generateTemplate.doGenerate(meta, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成失败！");
        }

        // 生成的文件有三类，一个是全量包，一个精简包（-dist）,一个压缩包（-dist.zip）下载生成的文件
        String suffix = "-dist.zip";
        String zipFilename = meta.getName() + suffix;
        String distZipFilePath = outputPath + suffix;
        System.out.println("distZipFilePath:" + distZipFilePath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFilename);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        //清理文件
        CompletableFuture.runAsync(() -> {
            //FileUtil.del(tempDirPath);
        });
    }

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }


    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 判断是否开启数据缓存
        String cacheKey = CacheManager.getPageCacheKey(generatorQueryRequest);
        if (generatorConfig.indexDataCacheEnable){
            //优先从缓存查询
            // 先拿到当前页的缓存key
//        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
//        String cacheValue = valueOperations.get(cacheKey);
            String cacheValue = cacheManager.get(cacheKey);
            if(StrUtil.isNotBlank(cacheValue)){
                // 命中，直接返回
                Page<GeneratorVO> generatorVOPage = JSONUtil.toBean(cacheValue, new TypeReference<Page<GeneratorVO>>(){}, false);
                return ResultUtils.success(generatorVOPage);
            }
        }

        // 缓存没有就查数据库
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        //如果前端不需要展示，那么查询的时候也不需要查询就可以了
        queryWrapper.select("id", "name", "description", "basePackage", "version", "author", "picture", "distPath", "status", "userId", "createTime", "updateTime");
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);
        // 这里前端并不需要展示这两个大字段，所以可以省略掉
//        generatorVOPage.getRecords().forEach(generatorVO -> {
//            generatorVO.setModelConfig(null);
//            generatorVO.setFileConfig(null);
//        });
        // 放入缓存
        //valueOperations.set(cacheKey, JSONUtil.toJsonStr(generatorVOPage), 100, TimeUnit.MINUTES);
        if (generatorConfig.indexDataCacheEnable){
            cacheManager.put(cacheKey, JSONUtil.toJsonStr(generatorVOPage), 5L);
        }

        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 分页获取当前生成器创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 编辑（生成器）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filePath = generator.getDistPath();
        if (StrUtil.isBlank(filePath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 日志
        log.info("用户 {} 下载了 {}", loginUser.getUserName(), filePath);
        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + filePath);

        //先找本地缓存
        String cacheFilePath = getCacheFilePath(id, filePath);
        if (FileUtil.exist(cacheFilePath)){
            Files.copy(Paths.get(cacheFilePath), response.getOutputStream());
            return;
        }
        FileUtil.touch(new File(cacheFilePath));

        // 下载
        COSObjectInputStream cosObjectInputStream = null;
        try {
            // 使用spring自带的工具查看执行耗时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInputStream = cosObject.getObjectContent();
            // 处理拿到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);
            stopWatch.stop();
            stopWatch.getTotalTimeMillis();

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

            //如果开启了下载后缓存到本地的开关，就写入本地
            if(generatorConfig.downloadCacheEnable){
                //todo 这里要注意，实际环境中，有几点注意。
                // 1、能够自动判断哪些需要缓存；
                // 2、支持手动清理缓存或定时清理缓存（有清理规则）；
                // 3、保证一致性，如果文件重新上传，应该也要更新缓存，或者如果生成器发生更新，就自动删除缓存。
                //这里写入本地的文件一直是空的，需要修改
                cosManager.download(filePath, cacheFilePath);
            }

        } catch (Exception e){
            log.error("file download error, filePath = " + filePath, e);
        } finally {
            if (cosObjectInputStream != null){
                cosObjectInputStream.close();
            }


        }
    }

    /**
     * 获取缓存文件路径
     *
     * @param id
     * @param distPath
     * @return
     */
    public String getCacheFilePath(long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        String zipFilePath = String.format("%s/%s", tempDirPath, distPath);
        return zipFilePath;
    }


    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {

        //第一步，需要登录
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(generatorUseRequest.getId());
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 获取生成器的路径 distpath
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        //定义独立的工作空间来存放临时文件
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, generatorUseRequest.getId());

        // 存放压缩包
        String zipFilePath = tempDirPath + ".zip";

        // 文件不存在，就创建文件
        if (!FileUtil.exist(zipFilePath)){
            FileUtil.touch(zipFilePath);
        }

        // 下载
        try {
            //先寻找缓存中是否存在
            String cacheFilePath = getCacheFilePath(generator.getId(), distPath);
            if (FileUtil.exist(cacheFilePath)){
                FileUtil.copy(cacheFilePath, zipFilePath, true);
            }else {
                cosManager.download(distPath, zipFilePath);
                //当本地缓存中不存在，就下载到本地缓存文件中，但是开启之后文件可能会越来越多，占用空间
                if(generatorConfig.downloadCacheEnable){
                    FileUtil.copy(zipFilePath,cacheFilePath , true);
                }
            }
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载文件失败");
        }

        // 解压文件
        File unZipFileDir = ZipUtil.unzip(zipFilePath);

        // 根据参数中的datamode.json文件写入到解压目录内
        String jsonStr = JSONUtil.toJsonStr(generatorUseRequest.getDataModel());
        System.out.println("dateModel参数：" + jsonStr);
        String dataModelFilePath = tempDirPath + "/datamodel.json";
        if (!FileUtil.exist(dataModelFilePath)){
            FileUtil.touch(dataModelFilePath);
        }
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);

        // 查找脚本（遍历文件两个层级，找到第一个文件名为generator的文件，没有找到就抛出异常）
        System.out.println("开始查找脚本文件");
        File scriptFile = FileUtil.loopFiles(unZipFileDir, 2, null).stream()
                .filter(file -> file.isFile() && "generator.bat".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        // 添加执行权限（win不需要）
//        try {
//            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
//            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
//        } catch (IOException e) {
//
//        }

        // 命令组装
        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String[] command = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};
        System.out.println("命令组装完成：" + Arrays.toString(command));

        // 找到脚本所在目录
        File scriptDir = scriptFile.getParentFile();

        //构建命令执行器
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // 进入到脚本所在目录
        processBuilder.directory(scriptDir);

        try {
            Process process = processBuilder.start();

            //读命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            //等待完成
            int exitCode = process.waitFor();
            System.out.println("命令执行完成，退出码为：" + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本错误");
        }

        //找到对应的生成的代码返回
        //生成代码的位置在脚本所在目录下的generated目录下
        String generatedDirPath = scriptDir.getAbsolutePath() + "/generated";
        //压缩生成的内容
        String resultZipPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedDirPath, resultZipPath);

        // 将压缩后的文件下载给用户
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        //写入响应
        Files.copy(resultFile.toPath(), response.getOutputStream());


        //清理文件
        CompletableFuture.runAsync(()->{
            //FileUtil.del(tempDirPath);
            System.out.println("delete temp file");
        });
    }





}
