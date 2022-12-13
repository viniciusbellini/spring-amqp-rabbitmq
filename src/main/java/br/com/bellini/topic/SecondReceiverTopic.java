package br.com.bellini.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SecondReceiverTopic {

	private static final String BINDING_KEY_NAME = "*.#";
	private static final String EXCHANGE_NAME = "TopicExchange";

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

		//O servidor irá determinar um nome randomico para a fila, que será temporária
		String nameQueue = channel.queueDeclare().getQueue();

		//Declaração da exchange
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		channel.queueBind(nameQueue, EXCHANGE_NAME, BINDING_KEY_NAME);

		//Escuta o canal onde está recebendo as mensagens
		DeliverCallback deliverCallback = (ConsumerTag, deliver) -> {
			String message = new String(deliver.getBody(), StandardCharsets.UTF_8);
			System.out.println("[*] Received message'" + message + "'");
		};

		channel.basicConsume(nameQueue, true, deliverCallback, ConsumerTag->{});
	}
}
