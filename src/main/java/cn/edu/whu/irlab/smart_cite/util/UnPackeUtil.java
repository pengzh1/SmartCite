package cn.edu.whu.irlab.smart_cite.util;

import com.github.junrar.Archive;
import com.github.junrar.VolumeManager;
import com.github.junrar.rarfile.FileHeader;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;


@Slf4j
public class UnPackeUtil {

    /**
     *@auther gcr19
     *@desc 解压zip文件
     *@param zipFile zip文件
     *@return 解压后的文件夹
     **/
    public static File unPackZip(File zipFile) throws ZipException {
        File folder = new File(zipFile.getParentFile().getPath()+"/" + FilenameUtils.getBaseName(zipFile.getName()));
        folder.mkdirs();
        ZipFile zip = new ZipFile(zipFile);
        /*zip4j默认用GBK编码去解压,这里设置编码为GBK的*/
        zip.setFileNameCharset("GBK");
        log.info("begin unpack zip file....");
        zip.extractAll(folder.getPath());
        return folder;
    }

    /**
     * rar文件解压(不支持有密码的压缩包)
     *
     * @param rarFile  rar压缩包
     * @param destPath 解压保存路径
     */
    public static void unPackRar(File rarFile, String destPath) {
        try (Archive archive = new Archive((VolumeManager) rarFile)) {
            if (null != archive) {
                FileHeader fileHeader = archive.nextFileHeader();
                File file = null;
                while (null != fileHeader) {
                    // 防止文件名中文乱码问题的处理
                    String fileName = fileHeader.getFileNameW().isEmpty() ? fileHeader.getFileNameString() : fileHeader.getFileNameW();
                    if (fileHeader.isDirectory()) {
                        //是文件夹
                        file = new File(destPath + File.separator + fileName);
                        file.mkdirs();
                    } else {
                        //不是文件夹
                        file = new File(destPath + File.separator + fileName.trim());
                        if (!file.exists()) {
                            if (!file.getParentFile().exists()) {
                                // 相对路径可能多级，可能需要创建父目录.
                                file.getParentFile().mkdirs();
                            }
                            file.createNewFile();
                        }
                        FileOutputStream os = new FileOutputStream(file);
                        archive.extractFile(fileHeader, os);
                        os.close();
                    }
                    fileHeader = archive.nextFileHeader();
                }
            }
        } catch (Exception e) {
            log.error("unpack rar file fail....", e.getMessage(), e);
        }
    }
}
