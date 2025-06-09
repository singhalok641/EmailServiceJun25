package com.example.emailservice.consumers;

import com.example.emailservice.dtos.SendEmailDto;
import com.example.emailservice.utils.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Component
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailDto emailDto =
                objectMapper.readValue(message, SendEmailDto.class);

        String to = emailDto.getEmail();
        String body = emailDto.getBody();
        String subject = emailDto.getSubject();

        // Send an email
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ipaddragon123@gmail.com", "fzdpqoeejhlrqrno");
            }
        };
        Session session = Session.getInstance(props, auth);
        EmailUtil.sendEmail(session, to,subject, body);
    }
}

/*

User sign up ->
User Service pushes an event to kafka ->
Email service listens to that event ->
Sends an email to the user who signed up

 */
