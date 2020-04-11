package cn.edu.whu.irlab.smart_cite.service.Unpack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 返回值处理
 * @Date: 2019/1/22
 * @Auther:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AjaxList<T> {
    private boolean isSuccess;
    private T data;

    public static <T> AjaxList<T> createSuccess(T data) {
        return new AjaxList<T>(true, data);
    }

    public static <T> AjaxList<T> createFail(T data) {
        return new AjaxList<T>(false, data);
    }
}
