---
title: Maven配置Java版本
author: doudio
date: 2019-03-19
categories: [maven]
tags: [maven]
---

## Maven配置Java版本

> 在pom文件中配置build节点

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

