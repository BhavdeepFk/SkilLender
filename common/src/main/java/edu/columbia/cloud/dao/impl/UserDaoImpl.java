package edu.columbia.cloud.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.cypher.internal.compiler.v2_1.docbuilders.internalDocBuilder;
import org.neo4j.shell.util.json.JSONException;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.db.neo4j.Neo4jUtils;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public class UserDaoImpl implements UserDao {
	Neo4jUtils neo4j = new Neo4jUtils();
	private static String USER_TYPE ="Person";
	private static String SKILL_TYPE ="Skill";
	private static String USER_SKILL_RELATIONSHIP ="has";
	private static String USER_USER_RELATIONSHIP ="knows";
	private static String USER_SKILL_RELATIONSHIP_PARAM="strength";
	
    @Override
    public boolean createUser(User user) {
    	Map<String, Object> propMap = new HashMap<String, Object>();
    	propMap.put("id", user.getId());
    	propMap.put("email", user.getEmail());
    	propMap.put("name", user.getName());
    	propMap.put("gender", user.getGender());
    	Date dob = user.getDob();
    	if(dob!=null)
    		propMap.put("dob",dob.getTime());
    	
    	User fetchUser = fetchUser(user.getId());
    	if(fetchUser!=null)
    		return false;
    	String nodeUrl = neo4j.createNode(propMap);
    	if(nodeUrl==null)
    		return false;
    	neo4j.addLabels(nodeUrl, USER_TYPE);
    	
    	//adding skills
    	 List<Skill> skillList = user.getSkillList();
    	 if(skillList!=null)
    	 for (Skill skill : skillList) {
 			String skillURL = neo4j.getNodeUrlByName(skill.getName());
 			if(skillURL == null){
 				skillURL = createSkill(skill);
 				if(skillURL == null)
 					return false;
 			}
 				
 			//adding relationship
 			neo4j.addRelationship(nodeUrl, skillURL, USER_SKILL_RELATIONSHIP , USER_SKILL_RELATIONSHIP_PARAM,skill.getLevel());
 		
		}
    	
    	//adding friends
    	List<User> friends = user.getConnections();
    	if(friends!=null)
    	for (User friend : friends) {
			//check if friend exists
    		String friendUrl = neo4j.getNodeUrlById(friend.getId());
    		if(friendUrl == null){
				createUser(friend);
				friendUrl = neo4j.getNodeUrlById(friend.getId());
			}
    		neo4j.addRelationship(nodeUrl, friendUrl, USER_USER_RELATIONSHIP);
		}   	
    	return true;
    }
    
    public String createSkill(Skill skill){
    	Map<String, Object> propMap = new HashMap<String, Object>();
    	propMap.put("id", skill.getId());
    	propMap.put("name", skill.getName());
    	propMap.put("category", skill.getCategory());;
    	String nodeUrl = neo4j.createNode(propMap);
    	if(nodeUrl==null)
    		return null;
    	neo4j.addLabels(nodeUrl, SKILL_TYPE);
    	
    	return nodeUrl;
    }
    
    @Override
    public User fetchUser(String userId, int level) {
    	try{
    	HashMap<String,Object> userProp = neo4j.getNodeById(userId);
    	Iterator<Entry<String, Object>> iterator = userProp.entrySet().iterator();
    	Object id = userProp.get("id");
    	Object name = userProp.get("name");
    	//String s=name==null?"":(String)name;
    	User user = new User((String)id, name==null?"":(String)name);
    	Object dob = userProp.get("dob");
    	if(dob!=null)
    	user.setDob(new Date(Long.parseLong((String)dob)));
    	Object gender = userProp.get("gender");
    	Object email = userProp.get("email");
    	user.setGender(gender==null?"":(String)gender);
    	user.setEmail(email==null?"":(String)email);
    	List<User> userList = new ArrayList<User>();
    	while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) iterator
					.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
		}
    	//add skills
    	 try {
			Map<String, Object> neighborsDeatilsOverRelation = neo4j.getNeighborsDeatilsOverRelation(userId, 1, "has");
			Iterator<Entry<String, Object>> iterator2 = neighborsDeatilsOverRelation.entrySet().iterator();
			while (iterator2.hasNext()) {
				Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) iterator2
						.next();
				Map<String, Object> skillMap =(Map<String, Object>)entry.getValue();
				Skill skill = new Skill();
				skill.setCategory((String)skillMap.get("category"));
				skill.setId((String)skillMap.get("id"));
				skill.setName((String)skillMap.get("name"));			
				
				//Adding strength part
				Map<String, Object> map =new HashMap<String, Object>();
				map.put("userId", "\""+userId+"\"");
				map.put("skillId", "\""+skill.getId()+"\"");
				String query="Match (xyz:Person {id:{userId}})-[r:has]-(skills:Skill {id :{skillId}})  return r.strength";
				String queryDB = neo4j.queryDB(query, map);
				Object object = neo4j.getDataFromColumns(queryDB).get("r.strength").get(0);
				if(object instanceof String)
					skill.setLevel(Integer.parseInt((String) object));
				else
					skill.setLevel((Integer)object);
				
				//Match (xyz:Person {name:"XYZ"})-[r:has]-(skills:Skill {id :{skillId}}) return r.strength
				
				
				user.addSkillToList(skill);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	
    	
    	if(level > 0){
    		level--;
    		//for each user run fetchUser( friendUserId, level);
    		List<Object> friendsIds = neo4j.getNeighborsOverRelation(userId, 1, "knows");
    		if(friendsIds!=null)
        	for (Object friendsId : friendsIds) {
    			User fetchUser = fetchUser((String)friendsId, level);
    			//add friends
    			user.addConnection(fetchUser);
    		}
    	}
    	
    	return user;
    	}catch(Exception e){
    		System.err.println("User fetch failed");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    @Override
    public User fetchUser(String userId) {
    	return fetchUser(userId, 0);
    }
    @Override
    public List<User> fetchUsersWithSkill(String userId, String skillId, int level) {
    	try{
    	List<User> users = new ArrayList<User>();
    	Map<String, Object> map =new HashMap<String, Object>();
		map.put("skillId", "\""+skillId+"\"");
		map.put("userId", "\""+userId+"\"");
		//Match (xyz:Person {id:"123"})-[:knows*1..1]-(friends)-[r:has]-(skills:Skill {id:"s2"}) where r.strength > "4" return friends.name,skills.name,r.strength
		String query="Match (xyz:Person {id:{userId}})-[:knows*1.."+level+"]-(friends)-[r:has]-(skills:Skill {id:{skillId}}) return friends.id";
		String queryDB = neo4j.queryDB(query, map);
		Map<String, List<Object>> dataFromColumns = neo4j.getDataFromColumns(queryDB);
		
		List<Object> list = dataFromColumns.get("friends.id");
		if(list!=null)
		for (Object object : list) {
			User user = fetchUser((String)object);
			users.add(user);
		}
		return users;
    	}catch(Exception e){
    		System.err.println("fetchUsersWithSkill failed");
    		e.printStackTrace();
    		return null;
    	}
    }
    @Override
    public List<User> fetchUsersWithSkill(String skillId) {
    	try{
    	List<User> users = new ArrayList<User>();
    	Map<String, Object> map =new HashMap<String, Object>();
		map.put("skillId", "\""+skillId+"\"");
		String query="Match (xyz:Person )-[r:has]-(skills) where skills.id={skillId} return xyz.id";
		String queryDB = neo4j.queryDB(query, map);
		Map<String, List<Object>> dataFromColumns = neo4j.getDataFromColumns(queryDB);
		List<Object> list = dataFromColumns.get("xyz.id");
		for (Object object : list) {
			User user = fetchUser((String)object);
			users.add(user);
		}
		return users;
    	}catch(Exception e){
    		System.err.println("fetchUsersWithSkill(String skillId) failed");
    		e.printStackTrace();
    		return null;
    	}
    }

	@Override
	public boolean addSkill(String userId, Skill skill, int strength) {
		String userUrl = neo4j.getNodeUrlById(userId);
		String skillUrl = neo4j.getNodeUrlByName(skill.getName());
		if(skillUrl == null){
			skillUrl = createSkill(skill);
			if(skillUrl == null)
				return false;
		}
		String addRelationship = neo4j.addRelationship(userUrl, skillUrl, USER_SKILL_RELATIONSHIP,USER_SKILL_RELATIONSHIP_PARAM,strength);
		if(addRelationship==null)
			return false;
		return true;
	}

	@Override
	public boolean removeSkill(String userId, String skillId) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("userId", "\""+userId+"\"");
		map.put("skillId", "\""+skillId+"\"");
		String query="Match (xyz:Person {id:{userId}})-[r:has]-(skills) where skills.id={skillId} delete r";
		String queryDB = neo4j.queryDB(query, map);
		if(queryDB==null)
			return false;
		return true;
		
	}
	
	@Override
	public boolean updateSkill(String userId, String skillId, int strength) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("userId", "\""+userId+"\"");
		map.put("skillId", "\""+skillId+"\"");
		map.put("strength", strength);
		String query="Match (xyz:Person {id:{userId}})-[r:has]-(skills) where skills.id={skillId} set r."+USER_SKILL_RELATIONSHIP_PARAM+"={strength} return r."+USER_SKILL_RELATIONSHIP_PARAM;
		String queryDB = neo4j.queryDB(query, map);
		if(queryDB==null)
			return false;
		return true;
	}

	@Override
	public boolean removeUser(String userId) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("userId", "\""+userId+"\"");
		String query="Match (xyz:Person {id:{userId}})-[r]-()  delete xyz, r";
		String queryDB = neo4j.queryDB(query, map);
		if(queryDB==null)
			return false;
		return true;
		
	}

	
}
