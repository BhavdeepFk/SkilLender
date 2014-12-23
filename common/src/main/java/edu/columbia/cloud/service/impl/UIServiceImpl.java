package edu.columbia.cloud.service.impl;

import edu.columbia.cloud.db.neo4j.Neo4jUtils;
import edu.columbia.cloud.service.UIService;

public class UIServiceImpl implements UIService {

	Neo4jUtils neo4jUtils;
	public UIServiceImpl() {
		// TODO Auto-generated constructor stub
		neo4jUtils=new Neo4jUtils();
	}
	@Override
	public String getD3Json() {
		// TODO Auto-generated method stub
		
		return null;
	}

}
