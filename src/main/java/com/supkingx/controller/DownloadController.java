package com.supkingx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

/**
 * @description:
 * @Author: wangchao
 * @Date: 2021/10/23
 */
@Controller
public class DownloadController {

    @GetMapping("/download")
    public void downloadFile(@RequestParam("downloadFile") String downloadFile, @RequestParam(value = "fileName", required = false) String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(downloadFile)) {
            throw new IllegalArgumentException("请输入文件路径!");
        }
        // 若没有指定文件下载后的名称，则默认使用文件真实名称
        fileName = StringUtils.isEmpty(fileName) ? getFileName(downloadFile) : fileName + getSuffix(downloadFile);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        File file = new File(downloadFile);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] b = new byte[1024];
            int length;
            while ((length = fileInputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }
        } catch (Exception e) {
            System.out.println("下载异常!" + e);
        }
    }

    /**
     * 获取文件后缀
     *
     * @param downloadFile
     * @return
     */
    private static String getSuffix(String downloadFile) {
        int indexOf = downloadFile.lastIndexOf(".");
        return downloadFile.substring(indexOf);
    }

    private static String getFileName(String downloadFile) {
        int indexOf = downloadFile.lastIndexOf("/");
        return downloadFile.substring(indexOf + 1);
    }
}
