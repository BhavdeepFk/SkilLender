package edu.columbia.cloud;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.service.SQSService;
import edu.columbia.cloud.service.UserService;
import edu.columbia.cloud.service.impl.SQSServiceImpl;
import edu.columbia.cloud.service.impl.UserServiceImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UpdateConnectionsExecutor implements Runnable {

    private Message message;
	private SQSService sqsService;
    private UserService userService;
    private static int WAIT_TIME = 1000;

	@Override
	public void run() {
		createConnections();
	}

	public UpdateConnectionsExecutor(Message message, SQSService sqsService, UserService userService){
		this.message = message;
        this.sqsService = sqsService;
        this.userService = userService;
	}

	private void createConnections() {
        try {
            String text = message.getBody();
            JSONObject jsonObject = new JSONObject(text);
            String accessTokenFrom = jsonObject.getString(Constants.ACCESS_TOKEN);
            String userIdFrom = jsonObject.getString(Constants.USER_ID);

            Connection<User> myFriends;
            FacebookClient facebookClient = new DefaultFacebookClient(accessTokenFrom, Constants.getInstance().getAppSecretKey());
            try{
                myFriends  = facebookClient.fetchConnection("me/friends", com.restfb.types.User.class);
            }catch (FacebookOAuthException e){
                FacebookClient.AccessToken token = facebookClient.obtainExtendedAccessToken(Constants.getInstance().getAppId(), Constants.getInstance().getAppSecretKey(), accessTokenFrom);
                facebookClient = new DefaultFacebookClient(token.getAccessToken(), Constants.getInstance().getAppSecretKey());
                myFriends  = facebookClient.fetchConnection("me/friends", com.restfb.types.User.class);
            }

            for (List<User> friends : myFriends) {
                for (User friend : friends) {
                    System.out.println("Creating connection: "+userIdFrom+":"+friend.getId());
                    userService.makeConnection(userIdFrom, friend.getId());
                }
            }




            //System.out.println("Count of my friends: " + myFriends.getData().size());
            //System.out.println(myFriends);

        }    catch (JSONException e){
            System.out.println("Error creating connection: "+e.getMessage());
        }finally {
            sqsService.deleteMessage(Constants.SL_QUEUE_URL, message.getReceiptHandle());
        }
    }
	
    public static void main(String[] args) throws InterruptedException, JSONException {
        System.out.println("Starting Connection Updation now!");
        WorkerBootStrap workerBootStrap = WorkerBootStrap.getInstance();
        workerBootStrap.startUp();
        SQSService sqsServiceIncoming = new SQSServiceImpl();
        UserService userServiceMain = new UserServiceImpl();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        while(true) {
            boolean worked = false;
            List<Message> msgList = sqsServiceIncoming.receiveMessage(Constants.SL_QUEUE_URL);
            for (Message msg : msgList) {
                worked = true;
                WAIT_TIME = 1000;
                Runnable updateConnectionsExecutor = new UpdateConnectionsExecutor(msg, sqsServiceIncoming, userServiceMain);
                executor.execute(updateConnectionsExecutor);

            }
            if(!worked){
                System.out.println("Waiting for "+WAIT_TIME/1000 +" seconds");
                Thread.sleep(WAIT_TIME);
                WAIT_TIME = WAIT_TIME * 2;
                if(WAIT_TIME > 64000){
                    WAIT_TIME =  64000;
                }

            }
        }

    }
}
