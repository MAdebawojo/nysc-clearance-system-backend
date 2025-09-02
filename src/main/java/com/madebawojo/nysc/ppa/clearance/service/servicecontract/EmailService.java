package com.madebawojo.nysc.ppa.clearance.service.servicecontract;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendEmailAsync(String to, String subject, String body);
    public void sendHtmlTemplateEmail(String to, String subject, String htmlContent) throws MessagingException;
}
