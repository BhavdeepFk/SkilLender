package edu.columbia.cloud.dao.impl;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;

import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean createUser(User user) {
        return false;
    }

    @Override
    public User fetchUser(String userId) {
        return null;
    }

    @Override
    public List<User> fetchUsersWithSkill(String skill) {
        return null;
    }

    @Override
    public boolean addSkill(String userId, Skill skill) {
        return false;
    }

    @Override
    public boolean removeSkill(String userId, Skill skill) {
        return false;
    }

    @Override
    public boolean updateSkill(String userId, Skill skill) {
        return false;
    }
}
