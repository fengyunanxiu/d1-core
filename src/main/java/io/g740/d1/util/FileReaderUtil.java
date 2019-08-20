package io.g740.d1.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/8/2 10:58
 * @param:   
 * @return:   
 */
public class FileReaderUtil {
    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        // 用来保存每行读取的内容
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        // 读取第一行
        line = reader.readLine();
        // 如果 line 为空说明读完了
        while (line != null) {
            // 将读到的内容添加到 buffer 中
            buffer.append(line);
            // 添加换行符
            buffer.append("\n");
            // 读取下一行
            line = reader.readLine();
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }


    public static void main(String[] args) throws IOException {
        String s = FileReaderUtil.readFile("d1-core/src/main/bin/sql/ORACLE.sql");
        String[] split = s.split("###");
        Arrays.asList(split).forEach(System.out::println);

    }
}