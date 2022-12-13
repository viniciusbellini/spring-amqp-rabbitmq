package br.com.bellini.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SenderRT {

	private static final String ROUTING_KEY = "routingKeyTest";
	private static final String SECOND_ROUTING_KEY = "secondRoutingKeyTest";
	private static final String EXCHANGE_NAME = "DirectExchange";

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
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");

			//criar a mensagem
			String message = "Hello! This is a RabbitMQ system!";
			String secondMessage = "Hello! This is a SECOND RabbitMQ system!";

			//enviar a mensagem, convertendo para bytes
			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
			channel.basicPublish(EXCHANGE_NAME, SECOND_ROUTING_KEY, null, secondMessage.getBytes());

			System.out.println("[*] Sent '" + message + "'");
			System.out.println("[*] Sent '" + secondMessage + "'");
		}
	}
}
