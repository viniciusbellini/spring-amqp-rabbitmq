package br.com.bellini.pubconfirmation;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SecondSenderPubConfirmation {

	private static final String EXCHANGE_NAME = "FanoutExchange";

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
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			//criar a mensagem
			String message = "This is my ";
			int setOfMessages = 10;
			int outMessages = 0;
			String bodyMessage;

			for (int i = 0; i < setOfMessages; i++) {
				bodyMessage = message + i;
				channel.basicPublish(EXCHANGE_NAME, "", null, bodyMessage.getBytes());
				System.out.println("[*] Sent '" + bodyMessage );
				outMessages++;

				if (outMessages == setOfMessages) {
					//wait for 5 seconds
					channel.waitForConfirmsOrDie(5_000);
					System.out.println("[v] Message confirmed!");
					outMessages = 0;
				}
			}

			if (outMessages != 0) {
				System.out.println();
				channel.waitForConfirmsOrDie(5_000);
				System.out.println("[v] Message confirmed!");
			}
			System.out.println("[*] Done!");
		}
	}
}
