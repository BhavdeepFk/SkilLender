package edu.columbia.cloud.rest;

import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.SearchService;
import edu.columbia.cloud.service.impl.SearchServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("search")
public class SearchREST {

    private SearchService searchService;

    public SearchREST(){
        searchService = new SearchServiceImpl();
    }


    @GET
    @Path("user/{userId}/{skillId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> fetch(@PathParam("userId") String userId, @PathParam("skillId") String skillId){
        return searchService.fetchUsersWithSkill(userId, skillId);
    }

    @GET
    @Path("connections/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> fetch(@PathParam("userId") String userId){
        return searchService.fetchUserConnections(userId);
    }


}
