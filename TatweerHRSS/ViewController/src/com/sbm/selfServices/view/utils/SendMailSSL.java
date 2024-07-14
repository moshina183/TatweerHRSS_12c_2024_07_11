package com.sbm.selfServices.view.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import oracle.security.jps.service.credstore.CredentialStore;
import oracle.security.jps.service.credstore.CredentialFactory;
import oracle.security.jps.service.credstore.Credential;
import oracle.security.jps.service.credstore.PasswordCredential;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
    String userName, password;

    public void CredentialStoreTest() {
        
        try {

            CredentialStore credentialStore =
                oracle.security.jps.service.JpsServiceLocator.getServiceLocator().lookup(CredentialStore.class);

            String map = "user.custom.map";
            String mykey = "myCSFKey";
//            try {
//                            System.out.println("Creating map " +map);
//                            if (credentialStore.containsMap(map)) {
//                                credentialStore.deleteCredentialMap(map);
//                            }
//                            credentialStore.setCredential(map, mykey, CredentialFactory.newPasswordCredential("alaa.osman200@gmail.com", "Welcome1".toCharArray()));
//                            System.out.println("Password set");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
            try {
                System.out.println("Accessing credential");
                Credential cred = credentialStore.getCredential(map, mykey);
                System.out.println("Password got:" + cred);
                if (cred != null) {
                    System.out.println("Password got:" +
                                       new String(((PasswordCredential)cred).getPassword()));
                    System.out.println("userName got:" +
                                       ((PasswordCredential)cred).getName());
                    password =
                            new String(((PasswordCredential)cred).getPassword());
                    userName = ((PasswordCredential)cred).getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }

    public void postEMail(String sendTo, HashMap subject) {
       // CredentialStoreTest();
        Properties props = new Properties();
        props.put("mail.smtp.ssl.enable", true);

        Authenticator auth = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(userName, password);

            }
        };


        Session session = Session.getInstance(props , auth);
        session.setDebug(true);
        try {
            InternetAddress[] address =
            { new InternetAddress("Essam.Hogbani@tatweer.sa") };
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("Essam.Hogbani@tatweer.sa"));
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("JavaMail APIs Test");
            msg.setSentDate(new Date());
            msg.setText("JavaMail APIs Test");

            Transport transport = session.getTransport("smtp");
            transport.connect(userName, password);
            //          Using authentication in an instance of Transport class instead of session
            transport.sendMessage(msg, address);
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }

    public void postMail(String sendTo, HashMap subject) {
        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");
        //  props.put("mail.smtp.socketFactory.port", "587");
        //   props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "587");
//        props.put("mail.smtp.starttls.enable", "true");

        // creates a new session, no Authenticator (will connect() later)
        //   Session session = Session.getDefaultInstance(props);


        Session session =
            Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("Essam.Hogbani@tatweer.sa",
                                                      "AlaaShetos90");
                }
            });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("Essam.Hogbani@tatweer.sa"));
            message.setRecipients(Message.RecipientType.TO,
                                  InternetAddress.parse("Essam.Hogbani@tatweer.sa"));
            message.setSubject("New Busniess Trip Request");
            String toPart = "Dear Sir," + "<br/><br/>";
            String bodyPart = "New Busniess Trip on The Cloud :- " + "<br/>";
            String bodyPart0 =
                "Trip Type :- " + (subject.get("trip") == null ? " " :
                                   subject.get("trip")) + "<br/>";
            String bodyPart1 =
                "Employee Name :- " + (subject.get("empName") == null ? " " :
                                       subject.get("empName")) + "<br/>";
            String bodyPart2 =
                "Employee Number :- " + (subject.get("empNumber") == null ?
                                         " " : subject.get("empNumber")) +
                "<br/>";
            String bodyPart3 =
                "start Date :- " + (subject.get("startDate") == null ? " " :
                                    subject.get("startDate")) + "<br/>";
            String bodyPart4 =
                "End Date        :- " + (subject.get("endDate") == null ? " " :
                                         subject.get("endDate")) + "<br/>";
            String bodyPart5 =
                "Number Of Days  :- " + (subject.get("days") == null ? " " :
                                         subject.get("days")) + "<br/>";


            String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
            String signaturePart =
                "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
                "<b/>" + "<br/>";
            String text =
                toPart + bodyPart + bodyPart0 + bodyPart1 + bodyPart2 +
                bodyPart3 + bodyPart4 + bodyPart5 + thankYouPart +
                signaturePart + "</p>";
            message.setContent(text, "text/html; charset=utf-8");
            //            Transport.send(message);

            Transport t = session.getTransport("smtp");
            t.connect("Essam.Hogbani@tatweer.sa", "Tatweer@123");
            t.sendMessage(message, message.getAllRecipients());
            t.close();

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws AddressException {

        //postMail("alaa.osman200@gmail.com", null);
    }
}
