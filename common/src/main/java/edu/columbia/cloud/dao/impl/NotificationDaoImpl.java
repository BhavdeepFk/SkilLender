package edu.columbia.cloud.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.columbia.cloud.dao.NotificationDao;
import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.models.Notification;
import edu.columbia.cloud.models.User;

public class NotificationDaoImpl implements NotificationDao {
	private Connection connection;
	
	public NotificationDaoImpl(){
		connection = Constants.getInstance().getMYSQLConnection(); 
	}
	
	
    //TODO: Write to RDS
    @Override
    public boolean sendNotification(Notification notification) {
    	String sql ="INSERT INTO notification VALUES(?,?,?,?,?)";
        try {
			PreparedStatement ps = connection.prepareStatement(sql);
			if(notification.getNotificationId()!=null)
				ps.setString(1, notification.getNotificationId());
			else
				ps.setString(1, null);
			if(notification.getUserIdFrom()!=null)
				ps.setString(2, notification.getUserIdFrom().getId());
			else 
				ps.setString(2,null);
			if(notification.getUserIdTo()!=null)
				ps.setString(3, notification.getUserIdTo().getId());
			else
				ps.setString(3,null);
			ps.setString(4, notification.getSkillName());
			ps.setString(5, notification.getStatus());
			return ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }

    //TODO: Read from RDS
    @Override
    public List<Notification> fetchNotification(String userId) {
    	String sql ="Select distinct(notificationId),skillName,status,userIdFrom,userIdTo from notification where userIdFrom like ? OR userIdTo like ? ";
    	List<Notification> notifications = new ArrayList<Notification>();
        try {
        	Notification userNotification = new Notification();
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, userId);
			ResultSet result = ps.executeQuery();
			System.out.println(result.getRow());
			while (result.next()) {
				userNotification = new Notification();
				userNotification.setNotificationId(result.getString("notificationId"));
				userNotification.setSkillName(result.getString("skillName"));
				userNotification.setStatus(result.getString("status"));
				userNotification.setUserIdFrom(new User(result.getString("userIdFrom"),null));
				userNotification.setUserIdTo(new User(result.getString("userIdTo"),null));	
				notifications.add(userNotification);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return notifications;
    }
}
