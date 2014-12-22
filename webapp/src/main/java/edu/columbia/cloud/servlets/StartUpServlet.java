package edu.columbia.cloud.servlets;

import edu.columbia.cloud.models.Constants;
import edu.columbia.cloud.service.SQSService;
import edu.columbia.cloud.service.impl.SQSServiceImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class StartUpServlet implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContextListener started");
        SQSService sqsService = new SQSServiceImpl();
        Constants.SL_QUEUE_URL =  sqsService.createQueue(Constants.SL_QUEUE_NAME);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContextListener destroyed");
    }
}