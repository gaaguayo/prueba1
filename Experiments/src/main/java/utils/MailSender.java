package utils;

/*
Some SMTP servers require a username and password authentication before you
can use their Server for Sending mail. This is most common with couple
of ISP's who provide SMTP Address to Send Mail.

This Program gives any example on how to do SMTP Authentication
(User and Password verification)

This is a free source code and is provided as it is without any warranties and
it can be used in any your code for free.

Author : Sudhir Ancha
*/

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/*
  To use this program, change values for the following three constants,

    SMTP_HOST_NAME -- Has your SMTP Host Name
    SMTP_AUTH_USER -- Has your SMTP Authentication UserName
    SMTP_AUTH_PWD  -- Has your SMTP Authentication Password

  Next change values for fields

  emailMsgTxt  -- Message Text for the Email
  emailSubjectTxt  -- Subject for email
  emailFromAddress -- Email Address whose name will appears as "from" address

  Next change value for "emailList".
  This String array has List of all Email Addresses to Email Email needs to be sent to.


  Next to run the program, execute it as follows,

  MailSender authProg = new MailSender();

*/

public class MailSender
{

//  private static final String SMTP_HOST_NAME = "smtpinterno.uam.es";
//  private static final String SMTP_HOST_PORT = "587";
//  private static final String SMTP_AUTH_USER = "pedro.campos@uam.es";
  private static final String SMTP_HOST_NAME = "smtp.gmail.com";
  private static final String SMTP_HOST_PORT = "465";
  private static final String SMTP_AUTH_USER = "pcampossoto@gmail.com";
  private static final String SMTP_AUTH_PWD  = "princesa@@";

  private static final String emailMsgTxt      = "Details";
  private static final String emailSubjectTxt  = "Finish";
  private static final String emailFromAddress = "pcampossoto@gmail.com";

  // Add List of Email address to who email needs to be sent to
  private static final String[] emailList = {"pcampossoto@gmail.com"};

  public static void main(String args[])
  {
    MailSender smtpMailSender = new MailSender();
    try{
    //    smtpMailSender.postMail( emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
        smtpMailSender.postMail( emailList, args[0], args[1], emailFromAddress);
    }
    catch (MessagingException e){
        System.err.println("Problem sending email report!");
    }
//    System.out.println("Sucessfully Sent mail to All Users");
  }

  public void postMail( String subject,
                            String message ) throws MessagingException
  {
      postMail(emailList, subject, message, emailFromAddress);
  }
  public void postMail( String recipients[ ], String subject,
                            String message , String from) throws MessagingException
  {
    boolean debug = false;

     //Set the host smtp address
     Properties props = new Properties();
     props.put("mail.smtp.host", SMTP_HOST_NAME);
     props.put("mail.smtp.port", SMTP_HOST_PORT);
     props.put("mail.smtp.auth", "true");
     props.put("mail.smtp.ssl.enable", "true");

    Authenticator auth = new SMTPAuthenticator();
    Session session = Session.getDefaultInstance(props, auth);

    session.setDebug(debug);

    // create a message
    Message msg = new MimeMessage(session);

    // set the from and to address
    InternetAddress addressFrom = new InternetAddress(from);
    msg.setFrom(addressFrom);

    InternetAddress[] addressTo = new InternetAddress[recipients.length];
    for (int i = 0; i < recipients.length; i++)
    {
        addressTo[i] = new InternetAddress(recipients[i]);
    }
    msg.setRecipients(Message.RecipientType.TO, addressTo);


    // Setting the Subject and Content Type
    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
 }


/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
private class SMTPAuthenticator extends javax.mail.Authenticator
{

    @Override
    public PasswordAuthentication getPasswordAuthentication()
    {
        String username = SMTP_AUTH_USER;
        String password = SMTP_AUTH_PWD;
        return new PasswordAuthentication(username, password);
    }
}

}

