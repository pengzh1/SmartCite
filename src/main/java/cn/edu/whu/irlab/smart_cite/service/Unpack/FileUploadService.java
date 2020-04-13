package cn.edu.whu.irlab.smart_cite.service.Unpack;



import cn.edu.whu.irlab.smart_cite.enums.ZipFileTypeEnum;
import cn.edu.whu.irlab.smart_cite.util.UnPackeUtil;
import cn.edu.whu.irlab.smart_cite.vo.PackParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
@Slf4j
public class FileUploadService  {


    public AjaxList<String> handlerUpload(MultipartFile zipFile, PackParam packParam) {

        if (null == zipFile) {
            return AjaxList.createFail("请上传压缩文件!");
        }
        boolean isZipPack = true;
        String fileContentType = zipFile.getContentType();
        //将压缩包保存在指定路径
        String packFilePath = packParam.getDestPath() + File.separator + zipFile.getName();
        if (ZipFileTypeEnum.FILE_TYPE_ZIP.type.equals(fileContentType)) {
            //zip解压缩处理
            packFilePath += ZipFileTypeEnum.FILE_TYPE_ZIP.fileStufix;
        } else if (ZipFileTypeEnum.FILE_TYPE_RAR.type.equals(fileContentType)) {
            //rar解压缩处理
            packFilePath += ZipFileTypeEnum.FILE_TYPE_RAR.fileStufix;
            isZipPack = false;
        } else {
            return AjaxList.createFail("上传的压缩包格式不正确,仅支持rar和zip压缩文件!");
        }
        File file = new File(packFilePath);
        try {
            zipFile.transferTo(file);
        } catch (IOException e) {
            log.error("zip file save to " + packParam.getDestPath() + " error", e.getMessage(), e);
            return AjaxList.createFail("保存压缩文件到:" + packParam.getDestPath() + " 失败!");
        }
        if (isZipPack) {
            //zip压缩包
            UnPackeUtil.unPackZip(file, packParam.getPassword(), packParam.getDestPath());
        } else {
            //rar压缩包
            UnPackeUtil.unPackRar(file, packParam.getDestPath());
        }
        return AjaxList.createSuccess("解压成功");
    }
}
