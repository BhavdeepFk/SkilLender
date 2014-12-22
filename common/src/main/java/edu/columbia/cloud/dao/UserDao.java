package edu.columbia.cloud.dao;

import java.util.List;

import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public interface UserDao {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public List<User> fetchUsersWithSkill(String skillId);

    public boolean addSkill(String userId, Skill skill, long strength);
    
    public boolean removeUser(String userId);

    public boolean removeSkill(String userId, String skillId);

    public boolean updateSkill(String userId, String skillId, long strength);

}
