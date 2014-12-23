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
    public boolean addUserSkill(String userId, Skill skill, int level) {
        return userDao.addSkill(userId, skill, level);
    }

    @Override
    public boolean removeUserSkill(String userId, String skillId) {
        return userDao.removeSkill(userId, skillId);
    }

    @Override
    public boolean updateUserSkill(String userId, String skillId, int level) {
        return userDao.updateSkill(userId, skillId, level);
    }

    @Override
    public boolean makeConnection(String userId, String anotherUserId) {
        return userDao.addConnection(userId, anotherUserId);
    }

	@Override
	public boolean updateUser(User user) {
		// TODO Auto-generated method stub
		return userDao.updateUser(user);
	}
}
