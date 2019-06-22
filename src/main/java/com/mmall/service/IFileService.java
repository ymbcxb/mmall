package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/6/22 14:56
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
