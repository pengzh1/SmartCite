package cn.edu.whu.irlab.smart_cite.enums;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Description: 压缩文件类型
 * @Date: 2019/1/22
 * @Auther:
 */
@AllArgsConstructor
@NoArgsConstructor
public enum ZipFileTypeEnum {
    FILE_TYPE_ZIP("application/x-zip-compressed", ".zip"),
    FILE_TYPE_RAR("application/octet-stream", ".rar");
    public String type;
    public String fileStufix;

    public static String getFileStufix(String type) {
        for (ZipFileTypeEnum orderTypeEnum : ZipFileTypeEnum.values()) {
            if (orderTypeEnum.type.equals(type)) {
                return orderTypeEnum.fileStufix;
            }
        }
        return null;
    }
}
