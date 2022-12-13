package br.com.bellini.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SecondReceiverPubSub {
	private static final String QUEUE_NAME = "broadcast";

	private static final String EXCHANGE_NAME = "FanoutExchange";

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
		System.out.println(channel);

		//Declaração da exchange
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		//declarar a fila que será utilizada
		//nome da fila, isExclusiva, isAutoDelete, isDurable, map(args)
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

		//Escuta o canal onde está recebendo as mensagens
		DeliverCallback deliverCallback = (ConsumerTag, deliver) -> {
			String message = new String(deliver.getBody(), StandardCharsets.UTF_8);
			System.out.println("[*] Received message'" + message + "'");
		};

		channel.basicConsume(QUEUE_NAME, true, deliverCallback, ConsumerTag->{});
	}
}
