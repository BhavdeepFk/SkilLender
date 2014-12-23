package edu.columbia.cloud.models;


import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import edu.columbia.cloud.db.mysql.DBConnection;

public class Constants {

    private String appId;
    private String appSecretKey;
    private static Constants instance = null;
    public static String SL_QUEUE_NAME = "skilLenderUser";
    public static String SL_QUEUE_URL = "";
    public static String ACCESS_TOKEN = "accessToken";
    public static String USER_ID = "userId";
    private String serverRootUri;// ="http://54.164.38.37:7474/";
    public static String  API_URL="db/data/node";
    public static String  CYPHER_URL="db/data/cypher/";
    public Connection connection;
	

    public String getNeo4jUri() {
		return serverRootUri;
	}

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
            serverRootUri = configs.getProperty("neo4j.uri");
            connection = DBConnection.getConnection(configs.getProperty("rds.uri"), configs.getProperty("rds.user"), configs.getProperty("rds.pwd"));        
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
