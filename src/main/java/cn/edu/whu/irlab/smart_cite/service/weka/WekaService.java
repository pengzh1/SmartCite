package cn.edu.whu.irlab.smart_cite.service.weka;

import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.*;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/23 23:45
 * @desc weka服务
 **/
@Service
public class WekaService {

    private static final Logger logger = LoggerFactory.getLogger(WekaService.class);

    private static final String MODEL_PATH = "result/model/libsvm.model";

    public Instances classify(String instancesPath) {
        Instances instances = loadInstances(instancesPath);
        LibSVM svm = reloadPersistModel();
        return classify(svm, instances);
    }

    public Instances trainAndClassify(String trainingDataPath,String instancesPath){
        Instances instances=loadInstances(instancesPath);
        LibSVM svm=train(trainingDataPath);
        return classify(svm,instances);
    }

    public Instances loadInstances(String instancesPath) {
        Instances data = null;

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(instancesPath);
            data = dataSource.getDataSet();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (data != null) {
            data.setClassIndex(data.numAttributes() - 1);
        }

        return data;
    }

    public Instances preprocessInstances(Instances instances) {
        //将部分属性从连续型变成离散型
        NumericToNominal toNominal = new NumericToNominal();
        String[] options = new String[0];
        try {
            options = weka.core.Utils.splitOptions("-R 1-7,11-last");
            toNominal.setOptions(options);
            toNominal.setInputFormat(instances);
            instances = weka.filters.Filter.useFilter(instances, toNominal);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return instances;
    }

    public LibSVM train(String trainingDataPath){
        Instances instances =loadInstances(trainingDataPath);
        instances=preprocessInstances(instances);
        return train(instances);
    }

    public LibSVM train(Instances instances) {
        LibSVM svm = new LibSVM();
        try {
            svm.buildClassifier(instances);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return svm;
    }

    public Instances classify(LibSVM svm, Instances instances) {
        for (Instance instance : instances) {
                double category = classify(svm, instance);
                instance.setClassValue(category);
        }
        return instances;
    }

    public double classify(LibSVM svm, Instance instance) {
        try {
            return svm.classifyInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void persistModel(LibSVM svm) {
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(MODEL_PATH));
            objectOutputStream.writeObject(svm);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LibSVM reloadPersistModel() {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(MODEL_PATH));
            return (LibSVM) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void outputInstances(Instances instances, String outputPath) {
        WriteUtil.writeStr(outputPath,instances.toString());
    }

}