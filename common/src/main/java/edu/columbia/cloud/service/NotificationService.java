package edu.columbia.cloud.service;

import com.sun.tools.corba.se.idl.constExpr.Not;
import edu.columbia.cloud.models.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(String userIdFrom, String userIdTo, String skillName);

    List<Notification> fetchNotification(String userId);
}
