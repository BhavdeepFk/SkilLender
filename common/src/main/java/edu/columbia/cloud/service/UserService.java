package edu.columbia.cloud.service;


import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

public interface UserService {

    public boolean createUser(User user);

    public User fetchUser(String userId);

    public boolean addUserSkill(String userId, Skill skill);

    public boolean removeUserSkill(String userId, Skill skill);

    public boolean updateUserSkill(String userId, Skill skill);

    public boolean makeConnection(String userId, String anotherUserId);

}
