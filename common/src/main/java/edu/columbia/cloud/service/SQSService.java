package edu.columbia.cloud.service;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;


public interface SQSService {

    String createQueue(String queueName);

    List<String> listQueues();

    void sendMessage(String queueUrl, String msg);

    List<Message> receiveMessage(String queueUrl);

    void deleteMessage(String queueUrl, String handleReceipt);

}
