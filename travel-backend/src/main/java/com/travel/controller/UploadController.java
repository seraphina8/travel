package com.travel.controller;

import com.travel.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    @Value("${file.upload-path:/uploads/}")
    private String uploadPath;

    // 通用文件上传接口
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择文件");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("头像大小不能超过2MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.error("文件名不合法");
        }

        String lowerName = originalFilename.toLowerCase();
        if (!(lowerName.endsWith(".jpg")
                || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".png"))) {
            return Result.error("头像只能上传JPG、JPEG或PNG格式图片");
        }
        
        try {
            // 获取扩展名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 使用UUID生成新文件名，防止重名覆盖
            String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

            // 构建上传目录的绝对路径
            String userDir = System.getProperty("user.dir");
            String realPath = userDir + "/uploads/";

            // 创建目录（如果不存在）
            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件到磁盘
            File destFile = new File(realPath + newFilename);
            file.transferTo(destFile);

            // 返回可访问的URL路径
            String url = "/uploads/" + newFilename;
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}
