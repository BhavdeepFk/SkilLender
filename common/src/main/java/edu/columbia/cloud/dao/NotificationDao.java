package edu.columbia.cloud.dao;

import edu.columbia.cloud.models.Notification;

import java.util.List;

public interface NotificationDao {

    boolean sendNotification(Notification notification);

    List<Notification> fetchNotification(String userId);
}
