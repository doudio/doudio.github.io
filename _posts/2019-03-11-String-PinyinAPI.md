---
title: String-PinyinAPI
author: doudio
date: 2019-03-11
categories: [JavaBase, java.lang]
tags: [JavaBase, java.lang]
---

> ## String PinyinAPI

> 导入第三方 `JAR`

```xml
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.1</version>
</dependency>
```

> 本地编写 PinyinUtils

```java
public class PinyinUtils {

    /**
    获取汉子首字母：zghw
    汉子转化为拼音：zhongguohuawei
    汉子转化为拼音：ZhongGuoHuaWei
    判断是否是一个字符：false
    */
    public static void main(String[] args) {
        System.out.println("获取汉子首字母："+getFirstSpell("中国华为"));
        System.out.println("汉子转化为拼音："+getPingYin("中国华为"));
        System.out.println("汉子转化为拼音："+getFullSpell("中国华为"));
        System.out.println("判断是否是一个字符："+"char".matches("[\\u4E00-\\u9FA5]+"));
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     */
    public static String getFirstSpell(String chinese) {
        // 用StringBuffer（字符串缓冲）来接收处理的数据
        StringBuffer sb = new StringBuffer();
        //字符串转换为字截数组
        char[] arr = chinese.toCharArray();
        //创建转换对象
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //转换类型（大写or小写）
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //定义中文声调的输出格式
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            //判断是否是汉子字符
            if (arr[i] > 128) {
                try {
                    // 提取汉字的首字母
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        sb.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                // 如果不是汉字字符，直接拼接
                sb.append(arr[i]);
            }
        }
        return sb.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     */
    public static String getPingYin(String inputString) {

        //创建转换对象
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        //转换类型（大写or小写）
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //定义中文声调的输出格式
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //定义字符的输出格式
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);

        //转换为字节数组
        char[] input = inputString.trim().toCharArray();
        // 用StringBuffer（字符串缓冲）来接收处理的数据
        StringBuffer output = new StringBuffer();

        try {
            for (int i = 0; i < input.length; i++) {
                //判断是否是一个汉子字符
                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                } else {
                    // 如果不是汉字字符，直接拼接
                    output.append(java.lang.Character.toString(input[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * 获取汉字串拼音，英文字符不变 【首字母大写】
     */
    public static String getFullSpell(String chinese) {
        // 用StringBuffer（字符串缓冲）来接收处理的数据
        StringBuffer sb = new StringBuffer();
        //字符串转换字节数组
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //转换类型（大写or小写）
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //定义中文声调的输出格式
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //定义字符的输出格式
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
        for (int i = 0; i < arr.length; i++) {
            //判断是否是汉子字符
            if (arr[i] > 128) {
                try {
                    sb.append(capitalize(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                // 如果不是汉字字符，直接拼接
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串转换成ASCII码
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        char ch[];
        ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        String newString = new String(ch);
        return newString;
    }

}
```

