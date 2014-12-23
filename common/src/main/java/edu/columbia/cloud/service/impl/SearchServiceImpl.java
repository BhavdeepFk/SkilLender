package edu.columbia.cloud.service.impl;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.SearchService;

import java.util.List;


public class SearchServiceImpl implements SearchService {

	private UserDao userDao;
	public SearchServiceImpl() {
		userDao = new UserDaoImpl();
	}
    @Override
    public List<User> fetchUsersWithSkill(String userId, String skill, int level) {
        return userDao.fetchUsersWithSkill(userId, skill, level);
    }

    @Override
    public List<User> fetchUserConnections(String userId) {
        User user = userDao.fetchUser(userId, 1);
        return user.getConnections();
    }
}
