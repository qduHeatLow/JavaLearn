package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "图片上传接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file 注意与前端提交的Body的参数名称"file"保持一直
     * @return 其中String类型的data是文件上传路径(阿里云绝对路径)
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}",file);
        try {
            String originalFilename = file.getOriginalFilename();
            //截取后缀名
            String fileNameEnd = originalFilename.substring(originalFilename.lastIndexOf('.'));
            //UUID
            String uuid = UUID.randomUUID().toString();
            String filePath = aliOssUtil.upload(file.getBytes(), uuid+fileNameEnd);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("图片上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
