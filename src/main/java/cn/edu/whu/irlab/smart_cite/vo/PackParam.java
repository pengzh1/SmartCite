package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @Description: 上传压缩的参数
 * @Date: 2019/1/23
 * @Auther:
 */
@Data
public class PackParam {
    /**
     * 解压密码
     */
    private String password;

    /**
     * 解压文件存储地址
     */
    private String destPath;
}
