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
import edu.columbia.cloud.service.UIService;
import edu.columbia.cloud.service.UserService;
import edu.columbia.cloud.service.impl.SQSServiceImpl;
import edu.columbia.cloud.service.impl.UIServiceImpl;
import edu.columbia.cloud.service.impl.UserServiceImpl;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("user")
public class UserREST {

    private UserService userService;
    private SQSService sqsService;
    private UIService uiService;
    private Gson gson;

    public UserREST(){
        userService = new UserServiceImpl();
        sqsService = new SQSServiceImpl();
        uiService = new UIServiceImpl();
        gson = new GsonBuilder().create();
    }


    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping(){
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", "pong");
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("create/{accessToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("accessToken") String accessToken){
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
        boolean result = userService.createUser(userInternal);
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("result", result);
        response.put("id", user.getId());
        return Response.ok().entity(response).build();
    }


    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("userId") String userId){
        User user = userService.fetchUser(userId);

        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        result.putPOJO("user", user);
        return Response.ok().entity(result).build();
    }


    @POST
    @Path("data/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("userId") String userId, String body) {

        System.out.println("Data Received: " + body);
        try {
            String bodyData = java.net.URLDecoder.decode(body.substring(7), "UTF-8");;
            JSONObject jsonObject = new JSONObject(bodyData);
            JSONArray allSkills = jsonObject.getJSONArray("allSkills");
            List<Skill> skillList = new ArrayList<Skill>();
            for(int counter = 0; counter<allSkills.length(); counter++){
                JSONObject skillJson = allSkills.getJSONObject(counter);
                Skill skill = new Skill();
                skill.setLevel(Integer.parseInt(skillJson.getString("level")));
                skill.setCategory(skillJson.getString("category"));
                skill.setName(skillJson.getString("name"));
                skillList.add(skill);
            }
            User user = new User(userId);
            user.setSkillList(skillList);

        }catch (UnsupportedEncodingException ue){

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", "pong");
        return Response.ok().entity(result).build();
    }


    @GET
    @Path("/graph")
    @Produces(MediaType.APPLICATION_JSON)
    public Response graph(){
        String graph = uiService.getD3Json();
        System.out.println(graph);

        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        result.putPOJO("graph", graph);
        return Response.ok().entity(result).build();
    }

    public static void main(String[] args) {
        UserREST userREST = new UserREST();
        userREST.graph();
    }

}
