package edu.columbia.cloud.dao.impl;

import edu.columbia.cloud.dao.NotificationDao;
import edu.columbia.cloud.models.Notification;

import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

    //TODO: Write to RDS
    @Override
    public void sendNotification(Notification notification) {

    }

    //TODO: Read from RDS
    @Override
    public List<Notification> fetchNotification(String userId) {
        return null;
    }
}
