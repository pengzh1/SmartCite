package cn.edu.whu.irlab.smart_cite.enums;

public enum XMLTypeEnum {

    Grobid(0), Plos(1),Lei(2),Json(3);

    private Integer code;

    XMLTypeEnum(Integer code) {
        this.code = code;
    }
}
