package edu.columbia.cloud.models;


import java.io.IOException;
import java.util.Properties;

public class Constants {

    private String appId;
    private String appSecretKey;
    private static Constants instance = null;
    public static String SL_QUEUE_NAME = "skilLenderUser";
    public static String SL_QUEUE_URL = "";
    public static String ACCESS_TOKEN = "accessToken";
    public static String USER_ID = "userId";



    public static Constants getInstance(){
        if(null == instance){
            instance = new Constants();
        }
        return instance;
    }

    private Constants(){
        Properties configs = new Properties();
        try {
            configs.load(Constants.class.getClassLoader().getResourceAsStream("configs.properties"));
            appSecretKey = configs.getProperty("fb.secretKey");
            appId = configs.getProperty("fb.accessId");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public String getAppId() {
        return appId;
    }

    public static void main(String[] args) {
        System.out.println(Constants.getInstance().appSecretKey);
    }
}
