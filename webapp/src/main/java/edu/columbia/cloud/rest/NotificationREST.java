package edu.columbia.cloud.rest;


import edu.columbia.cloud.models.Notification;
import edu.columbia.cloud.service.NotificationService;
import edu.columbia.cloud.service.impl.NotificationServiceImpl;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("notify")
public class NotificationREST {

    private NotificationService notificationService;

    public NotificationREST() {
        notificationService = new NotificationServiceImpl();
    }

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Object ping(){
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", "pong");
        return Response.ok().entity(result).build();
    }


    @GET
    @Path("{userIdFrom}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object fetch(@PathParam("userIdFrom") String userIdFrom){
        List<Notification> notificationList = notificationService.fetchNotification(userIdFrom);

        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", "pong");
        result.putPOJO("notifications", notificationList);
        return Response.ok().entity(result).build();
    }


    @POST
    @Path("{userIdFrom}/{userIdTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object send(@PathParam("userIdFrom") String userIdFrom, @PathParam("userIdTo") String userIdTo, @QueryParam("skillName") String skillName){
        notificationService.sendNotification(userIdFrom, userIdTo, skillName);
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("result", true);
        return Response.ok().entity(result).build();
    }

}
