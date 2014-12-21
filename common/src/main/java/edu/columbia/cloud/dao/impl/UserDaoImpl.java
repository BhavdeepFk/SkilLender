package edu.columbia.cloud.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.db.neo4j.Neo4jUtils;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public class UserDaoImpl implements UserDao {
	Neo4jUtils neo4j = new Neo4jUtils();
	private static String USER_TYPE ="Person";
	private static String SKILL_TYPE ="Skill";
	private static String USER_SKILL_REALTIONSHIP ="has";
	private static String USER_USER_REALTIONSHIP ="knows";
	
    @Override
    public boolean createUser(User user) {
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
			String skillURL = neo4j.getNodeUrl(skill.getId());
			if(skillURL == null){
				//create skill node
			}
				
			//adding relationship
			neo4j.addRelationship(nodeUrl, skillURL, USER_SKILL_REALTIONSHIP , entry.getValue());
		}
    	
    	//adding friends
    	List<User> friends = user.getFriends();
    	for (User friend : friends) {
			//check if friend exists
    		String friendUrl = neo4j.getNodeUrl(friend.getId());
    		if(friendUrl == null){
				createUser(friend);
				friendUrl = neo4j.getNodeUrl(friend.getId());
			}
    		neo4j.addRelationship(nodeUrl, friendUrl, USER_USER_REALTIONSHIP);
		}
    	
    	return true;
    }

    @Override
    public User fetchUser(String userId) {
    	return null;
    }

    @Override
    public List<User> fetchUsersWithSkill(String skill) {
        return null;
    }

    
    public boolean addSkill(String userId, Skill skill, Long strength) {
    	String nodeUrl = neo4j.getNodeUrl(userId);
    	String skillURL = neo4j.getNodeUrl(skill.getId());
    	if(skillURL == null){
			//create skill node
		}
    	
    	String addRelationship = neo4j.addRelationship(nodeUrl, skillURL, USER_SKILL_REALTIONSHIP, "strength",strength );
    	if(addRelationship == null)
    		return false;
    	return true;

    }
    @Override
    public boolean addSkill(String userId, Skill skill) {
    	return addSkill(userId, skill, 10L);
    }

    @Override
    public boolean removeSkill(String userId, Skill skill) {
        return false;
    }

    public boolean updateSkill(String userId, Skill skill,Long strength) {
        return false;
    }
    @Override
    public boolean updateSkill(String userId, Skill skill) {
        return updateSkill(userId, skill,10L);
    }
}
