package com.smc.johnny.main.mailer;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * <p>This class is responsible for sending customized mails to customized users.
 * <b>It must be updated according to the application in which it's used.</b>
 * smtpHost, fromAddress, fromName are examples of items that must be updated
 * accordingly. The message subject and body are parameters from the methods.</p>
 * 
 * @author smcoelho
 */
public class Mailer {
	
	private static Logger log = Logger.getRootLogger();
	private static final String screenshotFilename = "screenshot.jpg";
	private static final String debuglogFilename = "tasdebug.log";

	/**
	 * Sends an email with carbon copy to recipients passed as parameters.
	 * @param String mailTo
	 * @param String copyTo
	 * @param String messageSubject
	 * @param String messageBody
	 */
	public static void send(String mailTo, String copyTo, String messageSubject, String messageBody, Boolean includeAttachment) {

		try {
			log.info("Sending email");
			String smtpHost = "la.relay.ibm.com";
			String fromAddress = "tas@br.ibm.com";
			String fromName = "TAS - Control Center Unit";
			Properties props = System.getProperties();
			props.put("mail.smtp.host", smtpHost);
			Session session = Session.getDefaultInstance(props, null);
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(fromAddress, fromName));

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo, false));

			if (copyTo != null)
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(copyTo, false));

			message.setSubject(messageSubject);
			
			// Creating Multipart to allow include attachment
			Multipart mp = new MimeMultipart();
			
			// Creating main body part
			MimeBodyPart body1 = new MimeBodyPart();
			body1.setText(messageBody + "\n\n");
			
			// If we should include attachment, create the second body part, else use
			// only the main body part
			if(includeAttachment){
				// Screen shot
				MimeBodyPart body2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(screenshotFilename);
				
				body2.setDataHandler(new DataHandler(fds));
				body2.setFileName(fds.getName());
				
				// Debug file
				MimeBodyPart body3 = new MimeBodyPart();
				FileDataSource fds2 = new FileDataSource(debuglogFilename);
				
				body3.setDataHandler(new DataHandler(fds2));
				body3.setFileName(fds2.getName());
				
				mp.addBodyPart(body1);
				mp.addBodyPart(body2);
				mp.addBodyPart(body3);
			} else {
				mp.addBodyPart(body1);
			}
			
			message.setContent(mp);
			message.setSentDate(new Date());

			Transport.send(message);

		} catch (Exception e) {
			log.error("Error sending email", e);
		}
	}
	
	
	/**
	 * Sends an email without carbon copy to recipients passed as parameters.
	 * @param String mailTo
	 * @param String copyTo
	 * @param String messageSubject
	 * @param String messageBody
	 */
	public static void send(String mailTo, String messageSubject, String messageBody, Boolean includeAttachment) {

		send(mailTo, "",messageSubject, messageBody, includeAttachment);
		
	}
}
