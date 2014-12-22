package edu.columbia.cloud.dao.impl;


import java.util.*;
import java.util.Map.Entry;

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
    	return createUser(user, 1);
    }
    
    public boolean createUser(User user, int degree){
    	Map<String, Object> propMap = new HashMap<String, Object>();
    	propMap.put("id", user.getId());
    	propMap.put("email", user.getEmail());
    	propMap.put("name", user.getName());
    	propMap.put("gender", user.getGender());
    	propMap.put("dob", user.getDob().getTime());
    	String nodeUrl = neo4j.createNode(propMap);
    	if(nodeUrl==null)
    		return false;
    	neo4j.addLabels(nodeUrl, USER_TYPE);
    	
    	//adding skills
    	Map<Skill, Long> skillMap = user.getSkillMap();
    	Iterator<Entry<Skill, Long>> iterator = skillMap.entrySet().iterator();
    	while (iterator.hasNext()) {
			Map.Entry<edu.columbia.cloud.models.Skill, java.lang.Long> entry = (Map.Entry<edu.columbia.cloud.models.Skill, java.lang.Long>) iterator
					.next();
			
			//check if skill exists
			Skill skill = entry.getKey();
			String skillURL = neo4j.getNodeUrlByName(skill.getName());
			if(skillURL == null){
				if(!createSkill(skill))
					return false;
			}
				
			//adding relationship
			neo4j.addRelationship(nodeUrl, skillURL, USER_SKILL_RELATIONSHIP , entry.getValue());
		}
    	
    	//adding friends
    	List<User> friends = user.getConnections();
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

    
    private boolean createSkill(Skill skill){
    	Map<String, Object> propMap = new HashMap<String, Object>();
    	propMap.put("id", skill.getId());
    	propMap.put("name", skill.getName());
    	propMap.put("category", skill.getCategory());;
    	String nodeUrl = neo4j.createNode(propMap);
    	if(nodeUrl==null)
    		return false;
    	neo4j.addLabels(nodeUrl, SKILL_TYPE);
    	return true;
    }
    
    
    @Override
    public User fetchUser(String userId) {
    	HashMap<String,Object> userProp = neo4j.getNodeById(userId);
    	Iterator<Entry<String, Object>> iterator = userProp.entrySet().iterator();
    	User user = new User((String)userProp.get("id"), (String)userProp.get("name"));
    	user.setDob(new Date(Long.parseLong((String)userProp.get("dob"))));

    	user.setGender((String)userProp.get("gender"));
    	user.setEmail((String)userProp.get("email"));
    	List<User> userList = new ArrayList<User>();

    	while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) iterator
					.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
		}

    	return null;
    }

    @Override
    public List<User> fetchUsersWithSkill(String skill) {
        return null;
    }

	@Override
	public boolean addSkill(String userId, Skill skill, int strength) {
		String userUrl = neo4j.getNodeUrlById(userId);
		String skillUrl = neo4j.getNodeUrlByName(skill.getName());
		String addRelationship = neo4j.addRelationship(userUrl, skillUrl, USER_SKILL_RELATIONSHIP,USER_SKILL_RELATIONSHIP_PARAM,strength);
		if(addRelationship==null)
			return false;
		return true;
	}

	@Override
	public boolean removeSkill(String userId, String skillId) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("skillId", skillId);
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
		map.put("userId", userId);
		map.put("skillId", skillId);
		map.put("strength", strength);
		String query="Match (xyz:Person {id:{userId}})-[r:has]-(skills) where skills.id={skillId} set skills.strength={strength} return skills.strength";
		String queryDB = neo4j.queryDB(query, map);
		if(queryDB==null)
			return false;
		return true;
	}

	@Override
	public boolean removeUser(String userId) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("userId", userId);
		String query="Match (xyz:Person {id:{userId}})-[r]-()  delete xyz, r";
		String queryDB = neo4j.queryDB(query, map);
		if(queryDB==null)
			return false;
		return true;
		
	}

	
}
