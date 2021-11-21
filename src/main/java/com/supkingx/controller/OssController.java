package com.supkingx.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @Author: wangchao
 * @Date: 2021/10/23
 */
@Controller
public class OssController {

    @Value("${endpoint}")
    private String endpoint;
    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam("fileUrl") String fileUrl, @RequestParam("bucketName") String bucketName, @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObject = ossClient.getObject(bucketName, getObjectName(fileUrl));
        try (InputStream inputStream = ossObject.getObjectContent();
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] b = new byte[1024];
            int length;
            while ((length = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }
        } catch (Exception e) {
            System.out.println("下载异常!" + e);
        }
        ossClient.shutdown();
    }

    private static String getObjectName(String fileUrl) {
        try {
            List<String> list = Arrays.asList(fileUrl.split("/"));
            return list.subList(3, list.size()).stream().collect(Collectors.joining("/"));
        } catch (Exception e) {
            throw new IllegalArgumentException("url地址错误!", e);
        }
    }
}
