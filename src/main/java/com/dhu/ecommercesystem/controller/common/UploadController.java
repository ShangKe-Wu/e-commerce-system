package com.dhu.ecommercesystem.controller.common;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.util.NewBeeMallUtil;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author:WuShangke
 * @create:2022/7/23-14:19
 * 上传文件
 */
@Controller
@RequestMapping("/admin")
public class UploadController {
    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    //上传单个文件
    @ResponseBody
    @PostMapping("/upload/file")
    public Result upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws URISyntaxException {
        String newFilename = genNewFileName(file);
        File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);//文件上传地址
        //创建文件
        File destFile = new File(Constants.FILE_UPLOAD_DIC+newFilename);
        //如果文件上传地址（文件夹）不存在则创建一个文件夹
        try {
            if(!fileDirectory.exists()){
                if(!fileDirectory.mkdir()){
                    throw new IOException("创建文件夹失败，路径为："+Constants.FILE_UPLOAD_DIC);
                }
            }
            file.transferTo(destFile);
            Result successResult = ResultGenerator.genSuccessResult("文件上传成功");
            //地址回显
            successResult.setData(NewBeeMallUtil.getHost(new URI(request.getRequestURL() + "")) + "/upload/" + newFilename);
            return successResult;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }
    }

    //上传多个文件
    @ResponseBody
    @PostMapping("/upload/files")
    public Result uploadV2(HttpServletRequest request) throws URISyntaxException{
        List<MultipartFile> multipartFiles = new ArrayList<>();
        if(standardServletMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
            int total=0;
            while(iterator.hasNext()){
                //最多只能上传5个文件
                if(total>5){
                    return ResultGenerator.genFailResult("文件上传不能超过5个");
                }
                total++;
                MultipartFile file = multipartHttpServletRequest.getFile(iterator.next()) ;
                multipartFiles.add(file);
            }
        }
        //list为空
        if(CollectionUtils.isEmpty(multipartFiles)){
            return ResultGenerator.genFailResult("文件参数异常");
        }
        //文件超过5个
        if(multipartFiles!=null && multipartFiles.size()>5){
            return ResultGenerator.genFailResult("文件最多只能上传5个");
        }
        //生成文件名
        List<String> fileNames = new ArrayList<>(multipartFiles.size());
        //遍历multipartFiles
        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile multipartFile = multipartFiles.get(i);
            String newFileName = genNewFileName(multipartFile);
            File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
            File newFile = new File(Constants.FILE_UPLOAD_DIC+newFileName);
            try {
                if(!fileDirectory.exists()){
                    if(!fileDirectory.mkdir()){
                        throw new IOException("创建文件夹失败，路径为："+Constants.FILE_UPLOAD_DIC);
                    }
                }
                multipartFiles.get(i).transferTo(newFile);
                fileNames.add(NewBeeMallUtil.getHost(new URI(request.getRequestURI()+"")) + "/upload/"+newFileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResultGenerator.genFailResult("文件上传失败");
            }
        }
        Result success = ResultGenerator.genSuccessResult();
        success.setData(fileNames);
        return success;
    }

    //生成新的文件名
    public static String genNewFileName(MultipartFile file){
        //获取原本的文件名
        String filename = file.getOriginalFilename();
        //获取后缀
        String suffix = filename.substring(filename.lastIndexOf('.'));
        //生成新的文件名
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder temp = new StringBuilder();
        temp.append(simpleDateFormat.format(new Date())).append(r.nextInt(100)).append(suffix);
        String newFilename = temp.toString();
        return newFilename;
    }
}
