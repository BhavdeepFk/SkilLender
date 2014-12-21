package edu.columbia.cloud.service.impl;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.UserService;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    private UserDao userDao;
    @Override
    public boolean createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public User fetchUser(String userId) {
        return userDao.fetchUser(userId);
    }

    @Override
    public boolean addUserSkill(String userId, Skill skill) {
        return userDao.addSkill(userId, skill);
    }

    @Override
    public boolean removeUserSkill(String userId, Skill skill) {
        return userDao.removeSkill(userId, skill);
    }

    @Override
    public boolean updateUserSkill(String userId, Skill skill) {
        return userDao.updateSkill(userId, skill);
    }

    @Override
    public boolean makeConnection(String userId, String anotherUserId) {
        return false;
    }
}
