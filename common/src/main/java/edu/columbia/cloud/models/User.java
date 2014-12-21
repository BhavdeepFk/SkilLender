package edu.columbia.cloud.models;

import java.util.*;

public class User {

    private String id;
    private String name;
    private String gender;
    private Date dob;
    private String email;
    private Map<Skill, Long> skillMap;
    private List<User> connections;

    public User(String id, String name){
        this.id = id;
        this.name = name;
        this.connections = new ArrayList<User>();
        this.skillMap = new HashMap<Skill, Long>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<Skill, Long> getSkillMap() {
        return skillMap;
    }

    public void setSkillMap(Map<Skill, Long> skillMap) {
        this.skillMap = skillMap;
    }

    public void addSkillToMap(Skill skill, Long skillVal){
        this.skillMap.put(skill, skillVal);
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<User> getConnections() {
        return connections;
    }

    public void setConnections(List<User> connections) {
        this.connections = connections;
    }

    public void addConnection(User user){
        this.connections.add(user);
    }
}
