package edu.columbia.cloud.rest;

import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.SearchService;
import edu.columbia.cloud.service.impl.SearchServiceImpl;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("search")
public class SearchREST {

    private SearchService searchService;

    public SearchREST(){
        searchService = new SearchServiceImpl();
    }


    @GET
    @Path("user/{userId}/{skillName}/{level}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("userId") String userId, @PathParam("skillName") String skillName, @PathParam("level") int level){
        List<User> userList = searchService.fetchUsersWithSkill(userId, skillName, level);
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        result.putPOJO("users", userList);
        return Response.ok().entity(result).build();

    }

    @GET
    @Path("connections/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("userId") String userId){
        List<User> userList = searchService.fetchUserConnections(userId);
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        result.putPOJO("users", userList);
        return Response.ok().entity(result).build();
    }


}
