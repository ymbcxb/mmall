package com.mmall.service.impl;

import com.mmall.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service.impl
 * @date 2019/6/22 14:56
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //上传文件的名字
        String uploadFileName = UUID.randomUUID().toString()+ "" + fileExtensionName;
        logger.info("开始上传文件,上传的文件名:{},上传的路径:{},新文件名：{}",fileName,path,uploadFileName);
        //保存文件
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            // todo 将targetFile上传到FTP服务器上
            // todo 删除upload下的文件

        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }


        return targetFile.getName();
    }
}
