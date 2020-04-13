package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;


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
