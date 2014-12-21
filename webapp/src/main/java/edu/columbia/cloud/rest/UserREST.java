package edu.columbia.cloud.rest;


import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.UserService;
import edu.columbia.cloud.service.impl.UserServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("user")
public class UserREST {

    private UserService userService;

    public UserREST(){
        userService = new UserServiceImpl();
    }

    @POST
    @Path("me/{accessToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean create(@PathParam("accessToken") String accessToken){
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Constants.getInstance().getAppSecretKey());
        com.restfb.types.User user;
        try{
             user = facebookClient.fetchObject("me", com.restfb.types.User.class);
        }catch (FacebookOAuthException e){
            FacebookClient.AccessToken token = facebookClient.obtainExtendedAccessToken(Constants.getInstance().getAppId(), Constants.getInstance().getAppSecretKey(), accessToken);
            facebookClient = new DefaultFacebookClient(token.getAccessToken(), Constants.getInstance().getAppSecretKey());
        }
        user = facebookClient.fetchObject("me", com.restfb.types.User.class);

        User userInternal = new User(user.getId(),user.getName());
        userInternal.setEmail(user.getEmail());
        userInternal.setDob(user.getBirthdayAsDate());
        userInternal.setGender(user.getGender());
        Connection<com.restfb.types.User> myFriends = facebookClient.fetchConnection("me/friends", com.restfb.types.User.class);
        System.out.println("Count of my friends: " + myFriends.getData().size());
        System.out.println(myFriends);
        boolean result = userService.createUser(userInternal);
        return result;
    }


    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public User fetch(@PathParam("userId") String userId){
        return userService.fetchUser(userId);
    }





}
