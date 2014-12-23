import java.util.List;

import org.neo4j.shell.util.json.JSONException;

import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.db.neo4j.Neo4jUtils;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;


public class Test {
public static void main(String[] args) {
	UserDao dao = new UserDaoImpl();
	User user = new User("1", "XYZ");
	user.setEmail("@");
	user.setGender("female");
	
	User user2 = new User("2", "ABC");
	user2.setEmail("@");
	user2.setGender("female");
	
	User user3 = new User("3", "HJK");
	user3.setEmail("@");
	user3.setGender("female");
	
	User user4 = new User("4", "LLL");
	user4.setEmail("@");
	user4.setGender("female");
	
	Skill skill = new Skill();
	skill.setCategory("s");
	skill.setId("s1");
	skill.setName("java");
	
	Skill skill2 = new Skill();
	skill2.setCategory("s");
	skill2.setId("s2");
	skill2.setName("py");
	
	Skill skill3 = new Skill();
	skill3.setCategory("s");
	skill3.setId("s3");
	skill3.setName("scala");
	
	
	user.addConnection(user2);
	//user.addConnection(user4);
	
	user2.addConnection(user3);
	
	user4.addConnection(user3);
	skill.setLevel(10);
	user.addSkillToList(skill);
	skill2.setLevel(5);
	user.addSkillToList(skill2);
	
	
	skill3.setLevel(10);
	user2.addSkillToList(skill3);
	skill2.setLevel(10);
	user2.addSkillToList(skill2);
	
	
	skill3.setLevel(10);
	user4.addSkillToList(skill3);
	skill2.setLevel(5);
	user4.addSkillToList(skill2);
	skill.setLevel(2);
	user4.addSkillToList(skill);
	
	
	Neo4jUtils neo4jUtils = new Neo4jUtils();
	
	/*System.out.println(dao.createUser(user));
	System.out.println(dao.createUser(user4));
	user.addConnection(user4);
	user4.getSkillList().remove(skill);
	System.out.println(dao.updateUser(user3));
	System.out.println(dao.updateUser(user4));
	
	//edao.addSkill(user.getId(), skill3, 10);
	List<User> fetchUsersWithSkill = dao.fetchUsersWithSkill("s2");
	for (User user5 : fetchUsersWithSkill) {
		System.out.println(user5.getName());
	}
	dao.removeSkill(user.getId(), skill3.getId());
	//dao.removeUser("0");
	dao.updateSkill(user.getId(), skill.getId(), 1);
	//Neo4jUtils neo4jUtils = new Neo4jUtils();
	//neo4jUtils.deleteAll();
	List<User> fetchUsersWithSkill2 = dao.fetchUsersWithSkill("1", "scala", 1);
	System.out.println(fetchUsersWithSkill2.get(0).getName());
	User fetchUser = dao.fetchUser("10152876710411291");
	System.out.println(fetchUser.getName());
	
	User fetchUser2 = dao.fetchUser("123", 1);
	System.out.println();
	*/
	System.out.println(neo4jUtils.genJsonForD3());
}
}
