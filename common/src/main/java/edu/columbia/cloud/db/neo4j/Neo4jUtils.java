package edu.columbia.cloud.db.neo4j;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public class Neo4jUtils {
	
	private HttpClient client;
	private Constants constant;
	public void init(){
		client = new HttpClient();
		constant = Constants.getInstance(); 
	}
	
	public String genJsonForD3() {
		try{
			String query="Match n return n.id";
		
		String queryDB = queryDB(query, null);
		System.out.println(queryDB);
		JSONObject jsonObject = new JSONObject(queryDB);
		List<Object> list = getDataFromColumns(jsonObject.toString()).get("n.id");
		List<String> nodeIds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("{\"nodes\":[\n");
		StringBuilder sbLinks = new StringBuilder();
		sbLinks.append("],\"links\":[");
		UserDao dao = new UserDaoImpl();
		for (Object object : list) {
			if(nodeIds.contains((String)object))
					continue;
			User fetchUser = dao.fetchUser((String)object, 0);
			addGraphDataToHashMap(fetchUser, nodeIds, sbLinks,sb);
		}
		String nodeString = sb.substring(0, sb.length()-1);
		String sbString = sbLinks.substring(0, sbLinks.length()-1);
		sbString=nodeString+sbString+"]}";
		return sbString;
		}catch(Exception e){
			return"";
		}
	}
	
	
	public void addGraphDataToHashMap(User user,List<String> nodeIds, StringBuilder sbLinks, StringBuilder json){
		if (nodeIds.contains(user.getId()))
			return;
		json.append("\n{\"name\":\"")	;
		json.append(user.getName());
		json.append("\",\"group\":1},");
		nodeIds.add(user.getId());
		List<Skill> skillList = user.getSkillList();
		for (Skill skill : skillList) {
			if (nodeIds.contains(skill.getId()))
				continue;
			else{
			nodeIds.add(skill.getId());
			json.append("\n{\"name\":\"");
			json.append(skill.getName());
			json.append("\",\"group\":2},");
			}
			sbLinks.append("\n{\"source\":");
			sbLinks.append(nodeIds.indexOf(user.getId()));
			sbLinks.append(",\"target\":");
			sbLinks.append(nodeIds.indexOf(skill.getId()));
			sbLinks.append(",\"value\":"+skill.getLevel()+"},");
		}
		List<User> connections = user.getConnections();
		for (User user2 : connections) {
			addGraphDataToHashMap(user2, nodeIds, sbLinks, json);
			sbLinks.append("\n{\"source\":");
			sbLinks.append(nodeIds.indexOf(user.getId()));
			sbLinks.append(",\"target\":");
			sbLinks.append(nodeIds.indexOf(user2.getId()));
			sbLinks.append(",\"value\":"+10+"},");
		}		
	}
	
	public Map<String, Object> getNeighborsDeatilsOverRelation(String userId,int level,String...relation) throws JSONException{
		if(!checkServer())
			return null;
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("param1","\""+ userId +"\"");
		String query="Match (a {id:{param1}})-[";
		if(relation.length==1)
			query+=":"+relation[0];
		query+="*";
		query+="1.."+level;
		query+="]-(neighbor) RETURN neighbor";
		String queryDB = queryDB(query, map);
		
		JSONObject jsonObject = new JSONObject(queryDB);
		
		//jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
		HashMap<String, List<Object>> map2 = new HashMap<String, List<Object>>();
		//JSONArray columns=(JSONArray) ((JSONArray)jsonObject.get("columns"));
		JSONArray data=(JSONArray) ((JSONArray)jsonObject.get("data"));
		//System.out.println(data);
		map= new HashMap<String, Object>();
		for (int i = 0; i < data.length(); i++) {	
			JSONArray dataArray = (JSONArray)data.get(i);
			//System.out.println(dataArray);
			JSONObject jsonObject1 = (JSONObject)dataArray.get(0);
			HashMap<String, Object> skillMap = new  HashMap<String, Object>();
			JSONObject dataJSON = jsonObject1.getJSONObject("data");
			JSONObject metadataJSON = jsonObject1.getJSONObject("metadata");
			Iterator keys = dataJSON.keys();
			while (keys.hasNext()) {
				String object = (String) keys.next();
				skillMap.put(object, dataJSON.get(object));
			}
			Iterator keys2 = metadataJSON.keys();
			while (keys2.hasNext()) {
				String object = (String) keys2.next();
				if(!object.equals("id"))
					skillMap.put(object, metadataJSON.get(object));
			}
			map.put((String)skillMap.get("id"), skillMap);
			//HashMap<String,Object> convertJsonToMap = neo4jUtils.convertJsonToMap(((JSONObject)dataArray.get(0)).toString());
			//System.out.println(map);
			/*for (int k = 0; k < dataArray.length(); k++) {
				try{
				list = lists.get(k);
				}catch(Exception e){
					list= new ArrayList<Object>();
					lists.add(list);
				}
				list.add(dataArray.get(k));
			}
	*/
			}
		return map;
	}
	
	public void deleteAll(){
		if(!checkServer())
			return;
		String query="MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";
		queryDB(query, null);
	}
	
	public List<Object> getNeighborsOverRelation(String userId,int level,String...relation){
		if(!checkServer())
			return null;
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("param1","\""+ userId +"\"");
		String query="Match (a {id:{param1}})-[";
		if(relation.length==1)
			query+=":"+relation[0];
		query+="*";
		query+="1.."+level;
		query+="]-(neighbor) RETURN distinct neighbor.id";
		String queryDB = queryDB(query, map);
		Map<String, List<Object>> dataFromColumns = getDataFromColumns(queryDB);
		if(dataFromColumns.containsKey("neighbor.id"))
			return dataFromColumns.get("neighbor.id");	
		return null;
	}
	
	
	public Map<String, List<Object>> getDataFromColumns(String json){
		if(!checkServer())
			return null;
		HashMap<String, List<Object>> map2 = new HashMap<String, List<Object>>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray columns=(JSONArray) ((JSONArray)jsonObject.get("columns"));
			JSONArray data=(JSONArray) ((JSONArray)jsonObject.get("data"));
			List<List<Object>> lists = new ArrayList<List<Object>>();
			List<Object> list=null;
			for (int i = 0; i < data.length(); i++) {	
				JSONArray dataArray = (JSONArray)data.get(i);
				for (int k = 0; k < dataArray.length(); k++) {
					try{
					list = lists.get(k);
					}catch(Exception e){
						list= new ArrayList<Object>();
						lists.add(list);
					}
					list.add(dataArray.get(k));
				}
			}
			if(list!=null)
			for (int i = 0; i < columns.length(); i++) {
				String colName = columns.getString(i);
				map2.put(colName, lists.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map2;
	}
	public int getServerStatus(){
	    int status = 500;
	    try{
	         
	        String url = constant.getNeo4jUri();
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
            String nodePointUrl = constant.getNeo4jUri() + Constants.API_URL;
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
            System.out.println("Creating Node:"+jsonObject.toString());
            StringRequestEntity requestEntity = new StringRequestEntity(jsonObject.toString(),
                                                                        "application/json",
                                                                        "UTF-8");
            mPost.setRequestEntity(requestEntity);
            int satus = client.executeMethod(mPost);
            output = mPost.getResponseBodyAsString( );
            Header locationHeader =  mPost.getResponseHeader("location");
            location = locationHeader.getValue();
            mPost.releaseConnection( );
            System.out.println("Create Node status : " + satus);
           // System.out.println("location : " + location);
           // System.out.println("output : " + output);
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
			System.out.println("Property add status : " + satus);
			//System.out.println("output : " + output);
		}
		catch(Exception e){
			System.out.println("Exception in Property add  : " + e);
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
			//System.out.println(json);
			StringRequestEntity requestEntity = new StringRequestEntity(json,
	                                                    "application/json",
	                                                    "UTF-8");
			mPut.setRequestEntity(requestEntity);
			int satus = client.executeMethod(mPut);
			output = mPut.getResponseBodyAsString( );

			mPut.releaseConnection( );
			System.out.println("add label status : " + satus);
			//System.out.println("output : " + output);
			}catch(Exception e){
				System.out.println("Exception in add label : " + e);
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
			//System.out.println("from url : " + fromUrl);

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
			System.out.println("Realtionship status : " + satus);
			//System.out.println("location : " + location);
			//System.out.println("output : " + output);
			}catch(Exception e){
				System.out.println("Exception in creating Realtionshi: " + e);
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
		if(jsonAttributes.length%2==0 && jsonAttributes.length>0)
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
	
	public boolean deleteRelationship(String startNodeURI,
	        String endNodeURI) {
		if(!checkServer())
			return false;
		String output = null;
	    try{
	        String nodePointUrl = constant.getNeo4jUri() + Constants.CYPHER_URL;
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
	        sb.append("\"MATCH (n)OPTIONAL MATCH (n)-[r]-()DELETE n,r\"");
	        String json="";
	        /*if(map!=null && !map.isEmpty())
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
	        else*/
	        	json=sb.toString();
	       json+="}";
	        
	        
	        
	        //System.out.println(json);
	        StringRequestEntity requestEntity = new StringRequestEntity(json,
	                                                                    "application/json",
	                                                                    "UTF-8");
	        mPost.setRequestEntity(requestEntity);
	        int satus = client.executeMethod(mPost);
	        output = mPost.getResponseBodyAsString( );
	        //Header locationHeader =  mPost.getResponseHeader("location");
	       // location = locationHeader.getValue();
	        mPost.releaseConnection( );
	        System.out.println("deleteRel status : " + satus);
	        //System.out.println("location : " + location);
	        //System.out.println("output : " + output);
	    }catch(Exception e){
	    System.out.println("Exception in deleteRel  : " + e);
	    }

	    return true;
	}
	
	/*public boolean deleteQuery(){
		if(!checkServer())
			return null;
		String output = null;
	    try{
	        String nodePointUrl = SERVER_ROOT_URI + CYPHER_URL;
	        HttpClient client = new HttpClient();
	        PostMethod mPost = new PostMethod(nodePointUrl);

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
	*/
	public  String queryDB(String query, Map<String, Object> map){
		if(!checkServer())
			return null;
		String output = null;
	    try{
	        String nodePointUrl = constant.getNeo4jUri() + Constants.CYPHER_URL;
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
	        sb.append("\""+query+"\"");
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
	        
	        
	        
	       System.out.println("QUERY"+json);
	        StringRequestEntity requestEntity = new StringRequestEntity(json,
	                                                                    "application/json",
	                                                                    "UTF-8");
	        mPost.setRequestEntity(requestEntity);
	        int satus = client.executeMethod(mPost);
	        output = mPost.getResponseBodyAsString( );
	        //Header locationHeader =  mPost.getResponseHeader("location");
	       // location = locationHeader.getValue();
	        mPost.releaseConnection( );
	        System.out.println("Query DB status : " + satus);
	        //System.out.println("location : " + location);
	       // System.out.println("output : " + output);
	    }catch(Exception e){
	    System.out.println("Exception in Query DB: " + e);
	    }

	    return output;
	}
	
	public HashMap<String, Object> convertJsonToMap(String json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try{
			JSONObject jsonObject = new JSONObject(json);
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
				if(!object.equals("id"))
				map.put(object, metadataJSON.get(object));
			}
		}catch(Exception e)
		{
			System.err.println("Exception in Converting JSON to MAP");
			return null;
		}
		return map;
	}
	
	public HashMap<String, Object> getNodeById(String id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1","\""+ id+"\"");
		String queryDB = queryDB("Match (xyz {id:{param1}}) return xyz", map);
		//System.out.println(queryDB);
		return convertJsonToMap(queryDB);
	}
	
	public HashMap<String, Object> getNodeByLongId(Long id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", id+"");
		String queryDB = queryDB("Match (xyz {id:{param1}}) return xyz", map);
		//System.out.println(queryDB);
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
		map.put("param1", "\""+id+"\"");
		String queryDB = queryDB("Match (xyz {id:{param1}}) return xyz", map);
		String url="";
		try{
			JSONObject jsonObject = new JSONObject(queryDB);
			jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
			url=jsonObject.getString("self");
		}catch(Exception e)
		{
			System.err.println("Exception in getting url By ID");
			return null;
		}
		return url;
	}

	public String getNodeUrlByName(String name) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param1", "\""+name+"\"");
		String queryDB = queryDB("Match (xyz {name:{param1}}) return xyz", map);
		String url="";
		try{
			JSONObject jsonObject = new JSONObject(queryDB);
			jsonObject = (JSONObject)(((JSONArray) ((JSONArray)jsonObject.get("data")).get(0)).get(0));
			url=jsonObject.getString("self");
		}catch(Exception e)
		{
			System.err.println("Exception in getting url By Name");
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
