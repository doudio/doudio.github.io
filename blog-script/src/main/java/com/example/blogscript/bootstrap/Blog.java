package com.example.blogscript.bootstrap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.blogscript.config.BlogConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    public static final String TITLE_TEMPLATE = """
            ---
            title: %s
            author: %s
            date: %s
            categories: %s
            tags: %s
            ---

            """;

    @PostConstruct
    public void run() {
        log.info("blogConfig: {}", JSONUtil.toJsonPrettyStr(blogConfig));
        List<File> localGithubFile = FileUtil.loopFiles(blogConfig.getLocalGithub(), file -> file.isFile() && file.getName().endsWith(".md"));

        /*log.info("localGithubFile: {}, size: {}", JSONUtil.toJsonPrettyStr(localGithubFile), localGithubFile.size());*/

        localGithubFile.forEach(file -> {
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
    }


    /*@PostConstruct
    public void compressGithub() {
        List<File> localGithubFile = FileUtil.loopFiles(blogConfig.getLocalGithub());
        log.info("localGithubFile: {}, size: {}", JSONUtil.toJsonPrettyStr(localGithubFile), localGithubFile.size());
        localGithubFile.stream()
                .filter(file -> !file.toString().startsWith("F:\\github\\note\\.git"))
                .filter(file -> !file.toString().endsWith(".md"))
                .filter(file -> !file.toString().endsWith(".jpg"))
                .filter(file -> !file.toString().endsWith(".png"))
                .filter(file -> !file.toString().endsWith(".gif"))
                .forEach(itm -> {
                    log.info(itm.toString());
                    FileUtil.del(itm);
                });
    }*/

    // TODO: 2023/6/19 rename
    /*localGithubFile.stream().forEach(file -> {
        String name = FileUtil.getName(file);
        String group0 = ReUtil.getGroup0("^\\d+(?=\\D)", name);
        String randomDay = LocalDate.of(2019, RandomUtil.randomInt(1, 12), RandomUtil.randomInt(1, 20)).toString();

        String reName = name;
        if (StrUtil.isNotBlank(group0)) {
            String delFirst = ReUtil.delFirst("^\\d+(?=\\D)", name);
            reName = delFirst.startsWith("-") ? randomDay + delFirst :  randomDay + "-" + delFirst;
        } else {
            reName = randomDay + "-" + name;
        }
        FileUtil.rename(file, String.valueOf(new File(FileUtil.getParent(file, 1), reName)), false);
    });*/

}
