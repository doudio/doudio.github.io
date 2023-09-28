package com.example.blogscript.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: doudio
 * @Date: 2023/6/19
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogConfig {

    private String localGithub;
    private String localGitee;
    private String imgPrefix;
    private String githubBlog;
    private String targetDir;

}
