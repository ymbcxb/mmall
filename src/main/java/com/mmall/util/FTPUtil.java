package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.util
 * @date 2019/7/19 0:06
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.server.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.server.pass");

    private String ip;
    private int port;
    private String user;
    private String passwd;
    private FTPClient ftpClient;


    public FTPUtil(String ip, int port, String user, String passwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.passwd = passwd;
    }

    public static boolean uploadFile(List<File> fileList){
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始连接ftp服务器:{}",ftpIp);
        boolean result = ftpUtil.uploadFile("/img", fileList);
        logger.info("上传ftp的结果:{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) {
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.passwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
//                ftpClient.setBufferSize(1024);
                ftpClient.makeDirectory(remotePath);
//                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                uploaded = true;
            } catch (IOException e) {
                logger.error("上传文件异常");
                uploaded = false;
                e.printStackTrace();
            }finally {
                if(fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String passwd){
        ftpClient = new FTPClient();
        boolean success = false;
        try {
            ftpClient.connect(ip);
            success = ftpClient.login(user, passwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常");
            e.printStackTrace();
        }
        return success;
    }


    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
