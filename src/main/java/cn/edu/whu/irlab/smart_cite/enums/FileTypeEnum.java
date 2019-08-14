package cn.edu.whu.irlab.smart_cite.enums;

public enum FileTypeEnum {

    PDF(1,"PDF"),
    STANDARD_XML(2,"STANDARD_XML");

    private Integer code;

    private String fileType;

    FileTypeEnum(Integer code, String fileType) {
        this.code = code;
        this.fileType = fileType;
    }
}
