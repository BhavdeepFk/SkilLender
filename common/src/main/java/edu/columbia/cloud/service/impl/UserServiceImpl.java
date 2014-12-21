package edu.columbia.cloud.service.impl;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    private UserDao userDao;
    @Override
    public boolean createUser(User user) {
        return false;
    }

    @Override
    public User fetchUser(String userId) {
        return null;
    }

    @Override
    public boolean addUserSkill(String userId, Skill skill) {
        return false;
    }

    @Override
    public boolean removeUserSkill(String userId, Skill skill) {
        return false;
    }

    @Override
    public boolean updateUserSkill(String userId, Skill skill) {
        return false;
    }
}
