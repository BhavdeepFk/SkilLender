package edu.columbia.cloud.service;

import edu.columbia.cloud.models.User;

import java.util.List;

public interface SearchService {

    public List<User> fetchUsersWithSkill(String userId, String skillId, int level);//

    
    public List<User> fetchUserConnections(String userId);
}
