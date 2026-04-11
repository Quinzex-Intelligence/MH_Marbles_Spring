package com.qi.service;

import com.qi.dto.ContactRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final JavaMailSender mailSender;

    public void sendContactUsMail(ContactRequest request){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("mhmarble198@gmail.com");
       message.setSubject(request.getSubject());
       message.setReplyTo(request.getEmail());
       message.setText(
               "Name: " + request.getName() + "\n" +
                       "Email: " + request.getEmail() + "\n\n" +
                       "Phone Number: " + request.getMobileNumber() + "\n\n"+
                       "Message:\n" + request.getMessage()
       );
       mailSender.send(message);
    }
}
