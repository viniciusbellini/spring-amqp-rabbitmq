package br.com.bellini.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SenderTopic {

	private static final String ROUTING_KEY = "quick.white.rabbit";
	private static final String SECOND_ROUTING_KEY = "slow.green.turtle";
	private static final String EXCHANGE_NAME = "TopicExchange";

	public static void main(String[] args) throws IOException, TimeoutException {

		//primeiro criar a conexão e setar as informações necessárias
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("172.20.0.2");
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("pass123");
		connectionFactory.setPort(5672);

		try (Connection connection = connectionFactory.newConnection()) {

			//criar um novo canal
			Channel channel = connection.createChannel();

			//declarar a fila que será utilizada
			//nome da fila, isExclusiva, isAutoDelete, isDurable, map(args)
			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			//criar a mensagem
			String message = "Hello! This is a message of white quick rabbit!";
			String secondMessage = "Hello! This is a message of green slow turtle!";

			//enviar a mensagem, convertendo para bytes
			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
			channel.basicPublish(EXCHANGE_NAME, SECOND_ROUTING_KEY, null, secondMessage.getBytes());

			System.out.println("[*] Sent '" + message + "'");
			System.out.println("[*] Sent '" + secondMessage + "'");
		}
	}
}
