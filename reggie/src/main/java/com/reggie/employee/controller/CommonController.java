package com.reggie.employee.controller;


import com.reggie.employee.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info(file.toString());
        String  originalFilename= file.getOriginalFilename();
        String suffix =originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        File dir = new File(basePath);

        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath+name);
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes =new byte[1024];
            while((len = fileInputStream.read(bytes))!=-1){
                servletOutputStream.write(bytes,0,len);
                servletOutputStream.flush();
            }
            servletOutputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
