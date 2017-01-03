/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getcreditscore;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
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
    private final static String SENDING_QUEUE_NAME = "RuleBaseQueue";
    private final static String LISTENING_QUEUE_NAME = "CreditScoreQueue";

    static String message = "";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("datdb.cphbusiness.dk");
        factory.setUsername("Dreamteam");
        factory.setPassword("bastian");

        Connection connection = factory.newConnection();
        Channel sendingChannel = connection.createChannel();
        Channel listeningChannel = connection.createChannel();

        listeningChannel.queueDeclare(LISTENING_QUEUE_NAME, false, false, false, null);
        
        Consumer consumer = new DefaultConsumer(listeningChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                String[] arr = message.split(",");
                
                sendingChannel.queueDeclare(SENDING_QUEUE_NAME, false, false, false, null);

                String message = arr[0] + "," + creditScore(arr[0]) + "," + arr[1] + "," + arr[2] ;
                
                sendingChannel.basicPublish("", SENDING_QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");

            }
        };
        
        listeningChannel.basicConsume(LISTENING_QUEUE_NAME, true, consumer);
    }

    private static int creditScore(java.lang.String ssn) {
        org.bank.credit.web.service.CreditScoreService_Service service = new org.bank.credit.web.service.CreditScoreService_Service();
        org.bank.credit.web.service.CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }

}
