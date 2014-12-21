package edu.columbia.cloud.models;

public class Skill {
    String id;
    String name;
    String category;
    
    public Double getCompetency() {
		return competency;
	}

	public void setCompetency(Double competency) {
		this.competency = competency;
	}

	Double competency;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
