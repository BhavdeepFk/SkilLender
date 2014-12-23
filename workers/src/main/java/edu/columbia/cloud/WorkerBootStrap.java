package edu.columbia.cloud;

import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.service.SQSService;
import edu.columbia.cloud.service.impl.SQSServiceImpl;


public class WorkerBootStrap {

	private static WorkerBootStrap instance = null;

    public synchronized static WorkerBootStrap getInstance() {
        if(instance == null) {
            instance = new WorkerBootStrap();
        }
        return instance;
    }

    private WorkerBootStrap() {
    }

    public void startUp(){

		SQSService sqsService = new SQSServiceImpl();
        Constants.SL_QUEUE_URL = sqsService.createQueue(Constants.SL_QUEUE_NAME);
    }

}
