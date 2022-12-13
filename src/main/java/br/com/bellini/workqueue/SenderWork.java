package br.com.bellini.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SenderWork {

	private static final String QUEUE_NAME = "WORK";

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
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);

			//criar a mensagem
			String message = "..........";

			//enviar a mensagem, convertendo para bytes
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

			System.out.println("[*] Sent '" + message + "'");
		}
	}
}
