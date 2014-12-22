package edu.columbia.cloud.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.models.Skill;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.SQSService;
import edu.columbia.cloud.service.UserService;
import edu.columbia.cloud.service.impl.SQSServiceImpl;
import edu.columbia.cloud.service.impl.UserServiceImpl;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("user")
public class UserREST {

    private UserService userService;
    private SQSService sqsService;
    private Gson gson;

    public UserREST(){
        userService = new UserServiceImpl();
        sqsService = new SQSServiceImpl();
        gson = new GsonBuilder().create();
    }


    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Object ping(){
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", "pong");
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("create/{accessToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object create(@PathParam("accessToken") String accessToken){
        String sqsToken = accessToken;
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Constants.getInstance().getAppSecretKey());
        com.restfb.types.User user;
        try{
             user = facebookClient.fetchObject("me", com.restfb.types.User.class);
        }catch (FacebookOAuthException e){
            FacebookClient.AccessToken token = facebookClient.obtainExtendedAccessToken(Constants.getInstance().getAppId(), Constants.getInstance().getAppSecretKey(), accessToken);
            sqsToken = token.getAccessToken();
            facebookClient = new DefaultFacebookClient(token.getAccessToken(), Constants.getInstance().getAppSecretKey());
            user = facebookClient.fetchObject("me", com.restfb.types.User.class);
        }

        User userInternal = new User(user.getId(),user.getName());
        userInternal.setEmail(user.getEmail());
        userInternal.setDob(user.getBirthdayAsDate());
        userInternal.setGender(user.getGender());
        System.out.println(userInternal);
        Map<String, String> sqsMsg = new HashMap<String, String>();
        sqsMsg.put(Constants.ACCESS_TOKEN, sqsToken);
        sqsMsg.put(Constants.USER_ID, user.getId());
        String msg = gson.toJson(sqsMsg);
        sqsService.sendMessage(Constants.SL_QUEUE_URL, msg);
        //Connection<com.restfb.types.User> myFriends = facebookClient.fetchConnection("me/friends", com.restfb.types.User.class);
        //System.out.println("Count of my friends: " + myFriends.getData().size());
        //System.out.println(myFriends);

        //TODO Uncomment
        //boolean result = userService.createUser(userInternal);
        boolean result = true;
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("result", result);
        response.put("id", user.getId());
        return Response.ok().entity(response).build();
    }


    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("userId") String userId){
        //User user = userService.fetchUser(userId);
        User user = new User("927716317252688", "Bhavdeep Sethi");
        user.setGender("male");
        user.setEmail("believethehype@gmail.com");
        user.setDob(null);
        List<Skill> skillList = new ArrayList<Skill>();
        Skill skill1 = new Skill();
        skill1.setId("1");
        skill1.setName("JAVA");
        skill1.setCategory("Technology");
        skill1.setLevel(7);

        Skill skill2 = new Skill();
        skill2.setId("2");
        skill2.setName("Maggi");
        skill2.setCategory("Cooking");
        skill2.setLevel(8);

        Skill skill3 = new Skill();
        skill3.setId("3");
        skill3.setName("Football");
        skill3.setCategory("Sports");
        skill3.setLevel(9);


        skillList.add(skill1);
        skillList.add(skill2);
        skillList.add(skill3);
        user.setSkillList(skillList);

        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        result.putPOJO("user", user);
        return Response.ok().entity(result).build();
    }



}
