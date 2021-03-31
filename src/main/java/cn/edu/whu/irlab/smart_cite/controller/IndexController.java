package cn.edu.whu.irlab.smart_cite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转层
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/result.html")
    public String result() {
        return "result";
    }

}
