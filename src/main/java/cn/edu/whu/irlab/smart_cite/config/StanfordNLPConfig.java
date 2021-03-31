package cn.edu.whu.irlab.smart_cite.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/28 21:35
 * @desc stanfordNLP 配置类
 **/
@Configuration
public class StanfordNLPConfig {

    @Bean
    public StanfordCoreNLP pipeline() {
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");


        // build pipeline
        return new StanfordCoreNLP(props);
    }
}
