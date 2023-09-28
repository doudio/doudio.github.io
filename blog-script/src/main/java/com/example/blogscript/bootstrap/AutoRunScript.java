package com.example.blogscript.bootstrap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.blogscript.config.BlogConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Auther: qwl
 * @Date: 2023/9/27
 * @Description: 自动执行
 * 1. 跟新gitee仓库
 * 2. gitee > copy > github 提交同步github
 * 3. 格式转换提交到doudio.io仓库
 * git.exe push --progress "origin" note-script:note-script
 */
@Slf4j
@Component
public class AutoRunScript {

    @Resource
    private BlogConfig blogConfig;

    public static final String TITLE_TEMPLATE = """
        ---
        title: %s
        author: %s
        date: %s
        categories: %s
        tags: %s
        ---

        """;

    private static String GIT_PULL = """
    F: && cd "%s" && git pull
    """;

    private static String GIT_STATUS = """
    F: && cd "%s" && git status
    """;

    private static String GIT_ADD = """
    F: && cd "%s" && git ADD .
    """;

    private static String GIT_COMMIT = """
    F: && cd "%s" && git commit -m "%s"
    """;

    private static String GIT_PUSH = """
    F: && cd "%s" && git push
    """;

    @PostConstruct
    public void run() {
        log.info("pull gitee: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_PULL.formatted(blogConfig.getLocalGitee())));

        Arrays.stream(FileUtil.ls(blogConfig.getLocalGitee()))
                .filter(itm -> !".git".equals(itm.getName()))
                .forEach(itm -> {
                    File source = itm;
                    File target = new File(blogConfig.getLocalGithub());
                    FileUtil.copy(source, target, true);
                });

        FileUtil.loopFiles(blogConfig.getLocalGithub()).stream()
                .filter(file -> !file.toString().startsWith("F:\\github\\note\\.git"))
                .filter(file -> !file.toString().endsWith(".md"))
                .filter(file -> !file.toString().endsWith(".jpg"))
                .filter(file -> !file.toString().endsWith(".png"))
                .filter(file -> !file.toString().endsWith(".gif"))
                .forEach(itm -> {
                    log.info(itm.toString());
                    FileUtil.del(itm);
                });

        log.info("GIT_STATUS github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_STATUS.formatted(blogConfig.getLocalGithub())));
        log.info("GIT_ADD github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_ADD.formatted(blogConfig.getLocalGithub())));
        log.info("GIT_COMMIT github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_COMMIT.formatted(blogConfig.getLocalGithub(), "script syn")));
        log.info("GIT_PULL github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_PULL.formatted(blogConfig.getLocalGithub())));
        log.info("GIT_PUSH github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_PUSH.formatted(blogConfig.getLocalGithub())));

        FileUtil.loopFiles(blogConfig.getLocalGithub(), file -> file.isFile() && file.getName().endsWith(".md")).forEach(file -> {
            // 文件名/文件名上的日期/文件名不带后缀字符的
            String fileName = FileUtil.getName(file);
            String date = StrUtil.sub(fileName, 0, 10);
            String title = StrUtil.replace(StrUtil.replace(fileName, date + "-", ""), ".md", "");

            // 剔除本地磁盘上的父路径/文件的纯目录结构/组装好的分类与标签
            String fileDisk = StrUtil.subSuf(file.toString(), blogConfig.getLocalGithub().length() + 1);
            String dir = StrUtil.replace(fileDisk, "\\" + fileName, "");
            String categories = "[" + StrUtil.replace(dir,"\\", ", ") + "]";

            /*log.info("fileName: {}, dir: {}, categories: {}, date: {}", title, dir, categories, date);*/

            String fileContent = FileUtil.readUtf8String(file);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(TITLE_TEMPLATE, title, "doudio", date, categories, categories));
            List<String> imgLink = ReUtil.findAll(Pattern.compile("!\\[.{0,255}]\\(img.{0,255}\\.(png|jpg|gif)\\)"), fileContent, 0, new ArrayList<>());
            for (String link : imgLink) {
                String replace = StrUtil.replace(link, "\\", "/");
                String mdLocalLink = ReUtil.getGroup1(".{0,255}\\((.{0,255})\\)$", replace);
                String githubLink = blogConfig.getImgPrefix() + StrUtil.replace(dir,"\\", "/") + "/" + mdLocalLink;
                fileContent = StrUtil.replace(fileContent, link, StrUtil.replace(replace, mdLocalLink, githubLink));
            }
            builder.append(fileContent);
            FileUtil.writeUtf8String(String.valueOf(builder), new File(blogConfig.getTargetDir(), fileName));
        });

        log.info("GIT_STATUS github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_STATUS.formatted(blogConfig.getGithubBlog())));
        log.info("GIT_ADD github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_ADD.formatted(blogConfig.getGithubBlog())));
        log.info("GIT_COMMIT github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_COMMIT.formatted(blogConfig.getGithubBlog(), "script syn")));
        log.info("GIT_PULL github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_PULL.formatted(blogConfig.getGithubBlog())));
        log.info("GIT_PUSH github: {}", RuntimeUtil.execForStr("cmd.exe", "/c", GIT_PUSH.formatted(blogConfig.getGithubBlog())));
    }

}
