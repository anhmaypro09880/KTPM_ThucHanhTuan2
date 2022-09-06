import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;
import data.Person;
import helper.XMLConvert;

public class QueueSender {
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		Context ctx = new InitialContext(settings);
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination destination = (Destination) ctx.lookup("dynamicQueues/Test1");
		Destination destinationNhan = (Destination) ctx.lookup("dynamicQueues/tanTrong");
		Connection con = factory.createConnection("admin", "admin");
		con.start();
		Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		//create text message
		MessageConsumer receiver = session.createConsumer(destinationNhan);
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						System.out.println("Đã gửi " + txt);
						msg.acknowledge();
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println(om);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Scanner sc = new Scanner(System.in);
		String chuoi;
	    do {
			System.out.println("Nhập vào chuỗi bất kỳ: ");
		    chuoi = sc.nextLine();
		    
		    Message msg = session.createTextMessage(chuoi);
			producer.send(msg);
			Person p = new Person(1001, "Testing", new Date());
//			String xml = new XMLConvert<Person>(p).object2XML(p);
//			msg = session.createTextMessage(xml);
//			producer.send(msg);
	    } while (!chuoi.equals("exit"));
		
		
		
		
	    System.out.println("vo ne");
	    
		session.close();
		con.close();
		System.out.println("Finished...");
	}
}