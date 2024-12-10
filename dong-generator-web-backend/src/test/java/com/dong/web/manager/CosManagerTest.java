package com.dong.web.manager;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;


@SpringBootTest
public class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    public void deleteObject() {
        //System.out.println(cosManager);
        cosManager.deleteObject("/dong-1332760876/generator_make_template/1862071556895084545");
    }

    @Test
    public void deleteObjects() {
        cosManager.deleteObjects(Arrays.asList("generator_make_template/1862071556895084545/5rsvQE8q-test1209.zip",
                "generator_make_template/1862071556895084545/2jAbjN3m-testMake.zip"));
    }

    @Test
    public void deleteDir() {
        cosManager.deleteDir("/dong-1332760876/generator_picture/1862071556895084545/");
    }
}