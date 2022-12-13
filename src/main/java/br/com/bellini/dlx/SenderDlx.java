package br.com.bellini.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SenderDlx {

	private static final String EXCHANGE_NAME = "mainExchange";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

		//primeiro criar a conexão e setar as informações necessárias
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("172.20.0.2");
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("pass123");
		connectionFactory.setPort(5672);

		try (Connection connection = connectionFactory.newConnection()) {

			//criar um novo canal
			Channel channel = connection.createChannel();
			AMQP.Confirm.SelectOk selectOk = channel.confirmSelect();
			System.out.println(selectOk);

			//declarar a fila que será utilizada
			//nome da fila, isExclusiva, isAutoDelete, isDurable, map(args)
			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			//criar a mensagem
			String message = "Hello! This is the message!";
			String routingKey = "consumerBk";
			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			System.out.println("[*] Sent '" + message + "'");
			System.out.println("[*] Done!");
		}
	}
}
