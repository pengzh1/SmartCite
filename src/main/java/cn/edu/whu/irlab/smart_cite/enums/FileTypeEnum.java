package cn.edu.whu.irlab.smart_cite.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {

    PDF("application/pdf","PDF"),
    XML("application/xml","XML");

    private String mimeType;

    private String suffix;
}
