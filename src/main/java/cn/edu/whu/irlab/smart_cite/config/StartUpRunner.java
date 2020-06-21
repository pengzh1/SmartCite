package cn.edu.whu.irlab.smart_cite.config;

import cn.edu.whu.irlab.smart_cite.vo.FileLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/5/30 17:10
 * @desc 启动时预先加载
 **/
@Component
//@Order(value = 1)
public class StartUpRunner implements CommandLineRunner {

    @Value("${test.location}")
    private String ymlLocation;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("加载配置文件：" + ymlLocation);

        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");

        File temp = new File(System.getProperty("user.dir") + File.separator + "temp");
        mkdir(temp);


        File feature_file = new File(FileLocation.FEATURE_FILE);
        mkdir(feature_file);

        File upload_file = new File(FileLocation.UPLOAD_FILE);
        mkdir(upload_file);

        File reformatted = new File(FileLocation.REFORMATTED);
        mkdir(reformatted);

        File added = new File(FileLocation.ADDED);
        mkdir(added);

        File art = new File(FileLocation.ART);
        mkdir(art);

        File outPut = new File(FileLocation.OUTPUT);
        mkdir(outPut);

        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作执行完毕。<<<<<<<<<<<<<");
    }

    private boolean mkdir(File file) {
        if (!file.exists()) {
            return file.mkdir();
        } else if (file.isFile()) {
            return file.mkdir();
        } else {
            return false;
        }
    }

}
