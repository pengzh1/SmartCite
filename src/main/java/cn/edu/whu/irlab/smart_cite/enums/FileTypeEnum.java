package cn.edu.whu.irlab.smart_cite.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {

    PDF(1,"PDF"),
    XML(2,"XML");


    private Integer code;

    private String fileType;
}
