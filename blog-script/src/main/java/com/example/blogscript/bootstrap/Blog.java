package com.example.blogscript.bootstrap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.example.blogscript.config.BlogConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * @Auther: doudio
 * @Date: 2023/6/19
 * @Description:
 */
@Slf4j
@Component
public class Blog {

    @Resource
    private BlogConfig blogConfig;
    
    @PostConstruct
    public void run() {
        log.info("blogConfig: {}", JSONUtil.toJsonPrettyStr(blogConfig));
        List<File> localGithubFile = FileUtil.loopFiles(blogConfig.getLocalGithub(), (file) -> {
            return file.isFile() && file.getName().endsWith(".md");
        });
        log.info("localGithubFile: {}, size: {}", JSONUtil.toJsonPrettyStr(localGithubFile), localGithubFile.size());
    }
    

}
