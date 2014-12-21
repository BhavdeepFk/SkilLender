package edu.columbia.cloud.dao;

import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

import java.util.List;

public interface UserDao {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public List<User> fetchUsersWithSkill(String skill);

    public boolean addSkill(String userId, Skill skill);

    public boolean removeSkill(String userId, Skill skill);

    public boolean updateSkill(String userId, Skill skill);

}
