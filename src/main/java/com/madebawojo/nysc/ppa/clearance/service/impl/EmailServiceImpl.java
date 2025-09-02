package com.madebawojo.nysc.ppa.clearance.service.impl;

import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(User user, String resetLink) {
        String htmlTemplate = loadTemplate("reset-password.html");

        Map<String, String> placeholders = Map.of(
                "{{name}}", user.getFirstName(),
                "{{reset_link}}", resetLink
        );

        String content = replacePlaceholders(htmlTemplate, placeholders);

        log.info("Sending Reset Password Link to {}", user.getEmail());

        sendEmailAsync(user.getEmail(), "Reset Password Email Request", content);
    }

    /**
     * Sends an email asynchronously.
     *
     * @param to      recipient email address
     * @param subject subject of the email
     * @param htmlContent the HTML content to send
     */
    @Override
    @Async
    public void sendEmailAsync(String to, String subject, String htmlContent) {
        try {
            sendHtmlTemplateEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Async email send failed to '{}'", to, e);
            // Optional: notify fallback service, log to Sentry, etc.
        }
    }

    /**
     * Sends an HTML email to the specified recipient.
     * Retries up to 3 times with exponential backoff (2s → 4s → 8s).
     *
     * @param to          the recipient's email address
     * @param subject     the subject of the email
     * @param htmlContent the HTML template to send
     * @throws MessagingException if sending fails after retries
     */
    @Retryable(
            value = { MailException.class, MessagingException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendHtmlTemplateEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//            helper.setFrom(new InternetAddress("noreply@example.com"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content

            mailSender.send(message);
            log.info("HTML email successfully sent to '{}'", to);

        } catch (MessagingException | MailException e) {
            log.error("Failed to send HTML email to '{}'", to, e);
            throw e; // Triggers retry due to @Retryable
        }
    }

    public String loadTemplate(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/emails/" + filename);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load email template", e);
        }
    }

    public String replacePlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }


    //    /**
//     * Sends an email with retry on failure.
//     * Retries up to 3 times with exponential backoff.
//     *
//     * @param to      recipient email address
//     * @param subject subject of the email
//     * @param body    body content of the email
//     */
//    @Override
//    @Retryable(
//            value = { MailException.class, MessagingException.class },
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 2000, multiplier = 2)
//    )
//    public void sendEmail(String to, String subject, String body) {
//        log.info("Sending email to '{}'", to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//
//        try {
//            mailSender.send(message);
//            log.info("Email sent to '{}'", to);
//        } catch (MailException ex) {
//            log.error("Failed to send email to '{}'", to, ex);
//            throw ex; // Let Spring Retry handle this
//        }
//    }

//    public void sendHtmlEmail() throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//
//        message.setFrom(new InternetAddress("sender@example.com"));
//        message.setRecipients(MimeMessage.RecipientType.TO, "recipient@example.com");
//        message.setSubject("Test email from Spring");
//
//        String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
//                "<p>It can contain <strong>HTML</strong> content.</p>";
//        message.setContent(htmlContent, "text/html; charset=utf-8");
//
//        mailSender.send(message);
//    }

}
