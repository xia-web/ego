package com.ego.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PicService {
    /**
     * 文件上传
     * @param file Spring MVC 上传对象对象
     * @return
     */
    Map<String,Object> update(MultipartFile file);
}
