package edu.columbia.cloud.service;


import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public interface UserService {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public boolean addUserSkill(String userId, Skill skill, long strength);

    public boolean removeUserSkill(String userId, String skillId);

    public boolean updateUserSkill(String userId, String skillid, long strength);

    public boolean makeConnection(String userId, String anotherUserId);

}
