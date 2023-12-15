package com.soeun.GiftFunding.mail;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailComponent {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.sendname}")
    private String fromName;

    public void send(String toEmail, String toName,
        String title, String contents) {

        MimeMessagePreparator mimeMessagePreparator =
            new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper mimeMessageHelper =
                        new MimeMessageHelper(mimeMessage, true, "UTF-8");

                    InternetAddress from = new InternetAddress();
                    from.setAddress(fromEmail);
                    from.setPersonal(fromName);

                    InternetAddress to = new InternetAddress();
                    to.setAddress(toEmail);
                    to.setPersonal(toName);

                    mimeMessageHelper.setFrom(from);
                    mimeMessageHelper.setTo(to);
                    mimeMessageHelper.setSubject(title);
                    mimeMessageHelper.setText(contents, true);
                }
            };
        try {
            javaMailSender.send(mimeMessagePreparator);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
