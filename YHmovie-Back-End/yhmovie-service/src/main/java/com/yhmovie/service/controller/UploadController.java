package com.yhmovie.service.controller;

import com.yhmovie.common.enums.UploadType;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.utils.AliyunOSSOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j(topic = "UploadController")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final AliyunOSSOperator aliyunOSSOperator;

    @PostMapping("/user-avatar")
    public Result uploadAvatar(MultipartFile file) throws Exception {
        log.info("上传文件：{}", file);
        if (!file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            log.debug("原始文件名：{}", originalFileName);
            // 上传文件
            String url = aliyunOSSOperator.upload(file.getBytes(),
                    originalFileName,
                    UploadType.USER_AVATAR
            );
            return Result.success(url);
        }
        return Result.error("上传失败");
    }

}

