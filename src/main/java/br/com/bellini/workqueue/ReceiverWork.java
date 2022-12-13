package br.com.bellini.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class ReceiverWork {

	private static final String QUEUE_NAME = "PUBSUB";

	private static void doWork(String task) throws InterruptedException {
		for (char ch:task.toCharArray()) {
			if(ch == '.')
				Thread.sleep(1000);
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {

		//primeiro criar a conexão e setar as informações necessárias
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("172.20.0.2");
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("pass123");
		connectionFactory.setPort(5672);

		Connection connection = connectionFactory.newConnection();

		//criar um novo canal
		Channel channel = connection.createChannel();

		//declarar a fila que será utilizada
		//nome da fila, isExclusiva, isAutoDelete, isDurable, map(args)
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		DeliverCallback deliverCallback = (ConsumerTag, deliver) -> {
			String message = new String(deliver.getBody(), StandardCharsets.UTF_8);
			System.out.println("[*] Received message'" + message + "'");

			try {
				try {
					doWork(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				System.out.println("[*] Done");
			}
		};

		boolean autoack = true; //ack is false
		channel.basicConsume(QUEUE_NAME, autoack, deliverCallback, ConsumerTag->{});
	}
}
