/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getcreditscore;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Buhrkall
 */
public class GetCreditScore {

    /**
     * @param args the command line arguments
     */
    
    private final static String SENDING_QUEUE_NAME = "2";

    
    public static void main(String[] args) throws IOException, TimeoutException {
        
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setHost("datdb.cphbusiness.dk");
            factory.setVirtualHost("student");
            factory.setUsername("student");
            factory.setPassword("cph");
            
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
    
            channel.queueDeclare(SENDING_QUEUE_NAME, false, false, false, null);
            String message = "" + creditScore("222222-5555");
            channel.basicPublish("", SENDING_QUEUE_NAME, null, message.getBytes());
 
            System.out.println(" [x] Sent '" + message + "'");
    
            channel.close();
            connection.close();
         
    }

    private static int creditScore(java.lang.String ssn) {
        org.bank.credit.web.service.CreditScoreService_Service service = new org.bank.credit.web.service.CreditScoreService_Service();
        org.bank.credit.web.service.CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }
    
    
    
    
}
