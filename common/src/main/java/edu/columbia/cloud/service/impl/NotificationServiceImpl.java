package edu.columbia.cloud.service.impl;

import edu.columbia.cloud.dao.NotificationDao;
import edu.columbia.cloud.dao.UserDao;
import edu.columbia.cloud.dao.impl.AWSNotificationDaoImpl;
import edu.columbia.cloud.dao.impl.NotificationDaoImpl;
import edu.columbia.cloud.dao.impl.UserDaoImpl;
import edu.columbia.cloud.models.Notification;
import edu.columbia.cloud.models.User;
import edu.columbia.cloud.service.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private NotificationDao notificationDao;
    private UserDao userDao;
    private NotificationDao amazonSESDao;
    public NotificationServiceImpl() {
        notificationDao = new NotificationDaoImpl();
        userDao = new UserDaoImpl();
        amazonSESDao = new AWSNotificationDaoImpl();
    }

    @Override
    public void sendNotification(String userIdFrom, String userIdTo, String skillName) {
        User userFrom = userDao.fetchUser(userIdFrom);
        User userTo = userDao.fetchUser(userIdTo);
        Notification notification = new Notification();
        notification.setUserIdFrom(userFrom);
        notification.setUserIdTo(userTo);
        notification.setSkillName(skillName);
        notificationDao.sendNotification(notification);
        amazonSESDao.sendNotification(notification);
    }

    @Override
    public List<Notification> fetchNotification(String userId) {
        return notificationDao.fetchNotification(userId);
    }
}
