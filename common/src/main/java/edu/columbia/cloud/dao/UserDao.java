package edu.columbia.cloud.dao;

import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

import java.util.List;

public interface UserDao {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public List<User> fetchUsersWithSkill(String skillId);

    public boolean addSkill(String userId, String skillId);

    public boolean removeSkill(String userId, String skillId);

    public boolean updateSkill(String userId, String skillId);

}
