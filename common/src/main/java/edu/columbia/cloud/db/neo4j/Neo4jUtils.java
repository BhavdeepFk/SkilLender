package edu.columbia.cloud.db.neo4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONObject;

public class Neo4jUtils {
	
	private HttpClient client;
	private final String SERVER_ROOT_URI ="http://localhost:7474/";
	private final String  API_URL="db/data/node";
	private final String  CYPHER_URL="db/data/cypher/";
	
	public void init(){
		client = new HttpClient();
	}
	
	public int getServerStatus(){
	    int status = 500;
	    try{
	         
	        String url = SERVER_ROOT_URI;
	        HttpClient client = new HttpClient();
	        GetMethod mGet =   new GetMethod(url);
	        status = client.executeMethod(mGet);
	        mGet.releaseConnection( );
	    }catch(Exception e){
	    System.out.println("Exception in connecting to neo4j : " + e);
	    }
	 
	    return status;
	}

	
	public boolean checkServer(){
		if(client==null)
    		init();  
    	if(getServerStatus()!=200)
    		return false;
    	else return true;
	}
	
	public String createNode(){
		return createNode(null);
	}
	
	public String createNode(Map<String, Object> properties){
		if(!checkServer())
			return null;
    	String output = null;
        String location = null;
        try{
            String nodePointUrl = SERVER_ROOT_URI + API_URL;
            PostMethod mPost = new PostMethod(nodePointUrl);

            /**
             * set headers
             */
            Header mtHeader = new Header();
            mtHeader.setName("content-type");
            mtHeader.setValue("application/json");
            mtHeader.setName("accept");
            mtHeader.setValue("application/json");
            mPost.addRequestHeader(mtHeader);

            /**
             * set json payload
             */
            
            JSONObject jsonObject = new JSONObject(properties);
            System.out.println(jsonObject.toString());
            StringRequestEntity requestEntity = new StringRequestEntity(jsonObject.toString(),
                                                                        "application/json",
                                                                        "UTF-8");
            mPost.setRequestEntity(requestEntity);
            int satus = client.executeMethod(mPost);
            output = mPost.getResponseBodyAsString( );
            Header locationHeader =  mPost.getResponseHeader("location");
            location = locationHeader.getValue();
            mPost.releaseConnection( );
            System.out.println("satus : " + satus);
            System.out.println("location : " + location);
            System.out.println("output : " + output);
        }catch(Exception e){
        System.out.println("Exception in creating node in neo4j : " + e);
        }

        return location;
	}
	
	
	public void addProperty(String nodeURI, String propertyName, String propertyValue){	
		if(!checkServer())
			return ;
		String output = null;
		try{
			String nodePointUrl = nodeURI + "/properties/" + propertyName;
			HttpClient client = new HttpClient();
			PutMethod mPut = new PutMethod(nodePointUrl);

			/**
			 * set headers
			 */
			Header mtHeader = new Header();
			mtHeader.setName("content-type");
			mtHeader.setValue("application/json");
			mtHeader.setName("accept");
			mtHeader.setValue("application/json");
			mPut.addRequestHeader(mtHeader);

			/**
			 * set json payload
			 */
			String jsonString = "\"" + propertyValue + "\"";
			StringRequestEntity requestEntity = new StringRequestEntity(jsonString,
	                                                    "application/json",
	                                                    "UTF-8");
			mPut.setRequestEntity(requestEntity);
			int satus = client.executeMethod(mPut);
			output = mPut.getResponseBodyAsString( );

			mPut.releaseConnection( );
			System.out.println("satus : " + satus);
			System.out.println("output : " + output);
		}
		catch(Exception e){
			//System.out.println("Exception in creating node in neo4j : " + e);
		}

	}

	public void addLabels(String nodeURI,String...label){	
		if(!checkServer())
			return ;
		String output = null;

		try{
			String nodePointUrl = nodeURI + "/labels/" ;//+ propertyName;
			HttpClient client = new HttpClient();
			PutMethod mPut = new PutMethod(nodePointUrl);

				/**
				 * set headers
				 */
			Header mtHeader = new Header();
			mtHeader.setName("content-type");
			mtHeader.setValue("application/json");
			mtHeader.setName("accept");
			mtHeader.setValue("application/json");
			mPut.addRequestHeader(mtHeader);

			/**
			 * set json payload
			 */
			//String jsonString = "\"" + propertyValue + "\"";
			StringBuilder jsonString = new  StringBuilder();
			jsonString.append("[");
			for (String string : label) {
				jsonString.append("\"" + string + "\",");
			}
			String json = jsonString.substring(0, jsonString.length()-1);
			json+="]";// = "[\"" + label + "\"]";
			StringRequestEntity requestEntity = new StringRequestEntity(json,
	                                                    "application/json",
	                                                    "UTF-8");
			mPut.setRequestEntity(requestEntity);
			int satus = client.executeMethod(mPut);
			output = mPut.getResponseBodyAsString( );

			mPut.releaseConnection( );
			System.out.println("satus : " + satus);
			System.out.println("output : " + output);
			}catch(Exception e){
				System.out.println("Exception in creating node in neo4j : " + e);
		}


	}


	public String addRelationship(String startNodeURI,
	        String endNodeURI,
	        String relationshipType,
	        Object ...jsonAttributes ){
		if(!checkServer())
			return null;
		String output = null;
		String location = null;
		try{
			String fromUrl = startNodeURI + "/relationships";
			System.out.println("from url : " + fromUrl);

			String relationshipJson = generateJsonRelationship( endNodeURI,
	                                     relationshipType,
	                                     jsonAttributes );

			System.out.println("relationshipJson : " + relationshipJson);

			HttpClient client = new HttpClient();
			PostMethod mPost = new PostMethod(fromUrl);

			/**
			 * set headers
			 */
			Header mtHeader = new Header();
			mtHeader.setName("content-type");
			mtHeader.setValue("application/json");
			mtHeader.setName("accept");
			mtHeader.setValue("application/json");
			mPost.addRequestHeader(mtHeader);

			/**
			 * set json payload
			 */
			StringRequestEntity requestEntity = new StringRequestEntity(relationshipJson,
	                                             "application/json",
	                                             "UTF-8");
			mPost.setRequestEntity(requestEntity);
			int satus = client.executeMethod(mPost);
			output = mPost.getResponseBodyAsString( );
			Header locationHeader =  mPost.getResponseHeader("location");
			location = locationHeader.getValue();
			mPost.releaseConnection( );
			System.out.println("satus : " + satus);
			System.out.println("location : " + location);
			System.out.println("output : " + output);
			}catch(Exception e){
				System.out.println("Exception in creating node in neo4j : " + e);
			}

		return location;

	}

	private String generateJsonRelationship(String endNodeURL,
	                 String relationshipType,
	                 Object ... jsonAttributes) {
		if(!checkServer())
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNodeURL);
		sb.append("\", ");

		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		sb.append("\"");	String s="";
		if(jsonAttributes.length%2==0)
		 {
			sb.append(", \"data\" : {");
			
			for(int i = 0; i < jsonAttributes.length; i+=2) {
			sb.append("\""+jsonAttributes[i] +"\":\""+ jsonAttributes[i+1]+"\"");
			 // Miss off the final comma
				sb.append(", ");
			
		}
			 s= sb.substring(0, sb.length()-2);
			s+=" }";
				
		}
		else
			s=sb.toString();
		s+=" }";
		return s;
	}
	
	public  String queryDB(String query, Map<String, Object> map){
		if(!checkServer())
			return null;
		String output = null;
	    try{
	        String nodePointUrl = SERVER_ROOT_URI + CYPHER_URL;
	        HttpClient client = new HttpClient();
	        PostMethod mPost = new PostMethod(nodePointUrl);

	        /**
	         * set headers
	         */
	        Header mtHeader = new Header();
	        mtHeader.setName("content-type");
	        mtHeader.setValue("application/json");
	        mtHeader.setName("accept");
	        mtHeader.setValue("application/json");
	        mPost.addRequestHeader(mtHeader);

	        /**
	         * set json payload
	         */
	        StringBuilder sb = new StringBuilder();
	        sb.append("{\"query\":");
	        sb.append(query);
	        String json="";
	        if(map!=null && !map.isEmpty())
	        {
	        	sb.append(",");
	        	sb.append("\"params\":{");
	        	Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
	        	while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) iterator
							.next();
					sb.append("\""+entry.getKey()+"\":");
					sb.append(""+entry.getValue()+"");
					sb.append(",");
				}
	        	json = sb.substring(0, sb.length()-1);
	        	json+="}";
	        }
	        else
	        	json=sb.toString();
	       json+="}";
	        
	        
	        
	        System.out.println(json);
	        StringRequestEntity requestEntity = new StringRequestEntity(json,
	                                                                    "application/json",
	                                                                    "UTF-8");
	        mPost.setRequestEntity(requestEntity);
	        int satus = client.executeMethod(mPost);
	        output = mPost.getResponseBodyAsString( );
	        //Header locationHeader =  mPost.getResponseHeader("location");
	       // location = locationHeader.getValue();
	        mPost.releaseConnection( );
	        System.out.println("satus : " + satus);
	        //System.out.println("location : " + location);
	        System.out.println("output : " + output);
	    }catch(Exception e){
	    System.out.println("Exception in creating node in neo4j : " + e);
	    }

	    return output;
	}
	
	public HashMap<String, Object> convertJsonToMap(String json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try{
			JSONObject jsonObject = new JSONObject(json);
			
			map.clear();
			
			
			jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
			JSONObject dataJSON = jsonObject.getJSONObject("data");
			JSONObject metadataJSON = jsonObject.getJSONObject("metadata");
			Iterator keys = dataJSON.keys();
			while (keys.hasNext()) {
				String object = (String) keys.next();
				map.put(object, dataJSON.get(object));
			}
			Iterator keys2 = metadataJSON.keys();
			while (keys2.hasNext()) {
				String object = (String) keys2.next();
				map.put(object, metadataJSON.get(object));
			}
		}catch(Exception e)
		{
			System.err.println("Exception in getting url");
			return null;
		}
		return map;
	}
	
	public HashMap<String, Object> getNodeById(String id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", id);
		String queryDB = queryDB("\"Match (xyz {id:{param1}}) return xyz\"", map);
		return convertJsonToMap(queryDB);
	}
	
	public HashMap<String, Object> getNodeByName(String name) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", "\""+name+"\"");
		String queryDB = queryDB("\"Match (xyz {name:{param1}}) return xyz\"", map);
		return convertJsonToMap(queryDB);
	}
	
	public String getNodeUrlById(String id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", id);
		String queryDB = queryDB("\"Match (xyz {id:{param1}}) return xyz\"", map);
		String url="";
		try{
			JSONObject jsonObject = new JSONObject(queryDB);
			jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
			url=jsonObject.getString("self");
		}catch(Exception e)
		{
			System.err.println("Exception in getting url");
			return null;
		}
		return url;
	}

	public String getNodeUrlByName(String name) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", "\""+name+"\"");
		String queryDB = queryDB("\"Match (xyz {name:{param1}}) return xyz\"", map);
		String url="";
		try{
			JSONObject jsonObject = new JSONObject(queryDB);
			jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
			url=jsonObject.getString("self");
		}catch(Exception e)
		{
			System.err.println("Exception in getting url");
			return null;
		}
		return url;
	}
	/*public String searchDatabase(String nodeURI, String relationShip){
	    String output = null;

	    try{

	        TraversalDescription t = new TraversalDescription();
	        t.setOrder( TraversalDescription.DEPTH_FIRST );
	        t.setUniqueness( TraversalDescription.NODE );
	        t.setMaxDepth( 2 );
	        t.setReturnFilter( TraversalDescription.ALL );
	        t.setRelationships( new Relationship( relationShip, Relationship.BOTH ) );
	        System.out.println(t.toString());
	        HttpClient client = new HttpClient();
	        PostMethod mPost = new PostMethod(nodeURI+"/traverse/node");

	        *//**
	         * set headers
	         *//*
	        Header mtHeader = new Header();
	        mtHeader.setName("content-type");
	        mtHeader.setValue("application/json");
	        mtHeader.setName("accept");
	        mtHeader.setValue("application/json");
	        mPost.addRequestHeader(mtHeader);

	        *//**
	         * set json payload
	         *//*
	        StringRequestEntity requestEntity = new StringRequestEntity(t.toJson(),
	                                                                    "application/json",
	                                                                    "UTF-8");
	        mPost.setRequestEntity(requestEntity);
	        int satus = client.executeMethod(mPost);
	        output = mPost.getResponseBodyAsString( );
	        mPost.releaseConnection( );
	        System.out.println("satus : " + satus);
	        System.out.println("output : " + output);
	    }catch(Exception e){
	System.out.println("Exception in creating node in neo4j : " + e);
	    }

	    return output;
	}

*/
	
	



}
