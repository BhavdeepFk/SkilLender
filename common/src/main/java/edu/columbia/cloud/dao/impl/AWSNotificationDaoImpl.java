package edu.columbia.cloud.dao.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import edu.columbia.cloud.dao.NotificationDao;
import edu.columbia.cloud.models.Notification;
import edu.columbia.cloud.models.User;

import java.util.List;

public class AWSNotificationDaoImpl implements NotificationDao {
    private AmazonSimpleEmailServiceClient amazonSES;
    private static String FROM_EMAIL = "skilender@googlegroups.com";
    private static String SUBJECT = "SkilLender User wants your help!";
    private static String BODY = "Hello %s, \n\n SkilLender user, %s wants your help with %s! The user can be reached at %s. \n\n\n Regards, \nSkilLender Team";


    public AWSNotificationDaoImpl() {
        AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
                    AWSNotificationDaoImpl.class.getClassLoader().getResourceAsStream("AwsCredentials.properties"));

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +e);
        }

        amazonSES = new AmazonSimpleEmailServiceClient(credentials);
        Region REGION = Region.getRegion(Regions.US_EAST_1);
        amazonSES.setRegion(REGION);

    }

    @Override
    public boolean sendNotification(Notification notification) {

        // Create the subject and body of the message.
        try{
            Content subject = new Content().withData(SUBJECT);
            String bodyContent = String.format(BODY, notification.getUserIdTo().getName(), notification.getUserIdFrom().getName(), notification.getSkillName(), notification.getUserIdFrom().getEmail());
            Content textBody = new Content().withData(bodyContent);
            Body body = new Body().withText(textBody);


            Message message = new Message().withSubject(subject).withBody(body);
            Destination destination = new Destination().withToAddresses(new String[]{notification.getUserIdTo().getEmail()});
            SendEmailRequest request = new SendEmailRequest().withSource(FROM_EMAIL).withDestination(destination).withMessage(message);
            amazonSES.sendEmail(request);
            System.out.println("Sending email to: "+notification.getUserIdTo().getEmail());
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    @Override
    public List<Notification> fetchNotification(String userId) {
        //Do nothing
        return null;
    }

  
}
