package fr.cerema.dsi.geremi.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${info.app.mail.host}")
  private String host;

  @Value("${info.app.mail.port}")
  private int port;

  @Value("${info.app.mail.username}")
  private String usersname;

  @Value("${info.app.mail.password}")
  private String password;

  @Value("${info.app.mail.tls}")
  private String tls;

  @Value("${info.app.mail.auth}")
  private String auth;


  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);

    mailSender.setProtocol("smtp");

    mailSender.setUsername(usersname);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    if (auth != null && !auth.isEmpty()) {
      props.put("mail.smtp.auth", auth);
    }
    if (tls != null && !tls.isEmpty()) {
      props.put("mail.debug", tls);
    }
    return mailSender;
  }
}
