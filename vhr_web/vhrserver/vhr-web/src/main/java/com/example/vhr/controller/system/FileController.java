package com.example.vhr.controller.system;

import com.example.vhr.utils.FastDFSUtil;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;




@RestController
public class FileController {

    @Value("${fastdfs.nginx.host}")
    String nginxHost;

    @PostMapping("/file")
    public String uploadFile(MultipartFile file) throws IOException, MyException {
        String fileId = FastDFSUtil.upload(file);
        String url = nginxHost + fileId;
        return url;
    }
}




