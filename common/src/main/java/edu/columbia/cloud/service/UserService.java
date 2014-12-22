package edu.columbia.cloud.service;


import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public interface UserService {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public boolean addUserSkill(String userId, Skill skill, int level);

    public boolean removeUserSkill(String userId, String skillId);

    public boolean updateUserSkill(String userId, String skillId, int level);

    public boolean makeConnection(String userId, String anotherUserId);

}
