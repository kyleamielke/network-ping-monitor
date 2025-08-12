package io.thatworked.support.notification.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.config.TemplateConfig;
import io.thatworked.support.notification.domain.model.NotificationChannel;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;
import io.thatworked.support.notification.domain.port.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

/**
 * Email implementation of the NotificationSender port.
 */
@Component
public class EmailNotificationSender implements NotificationSender {
    
    private final StructuredLogger logger;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TemplateConfig templateConfig;
    
    @Value("${notification-service.email.from}")
    private String fromEmail;
    
    @Value("${notification-service.email.charset:UTF-8}")
    private String charset;
    
    public EmailNotificationSender(StructuredLoggerFactory loggerFactory,
                                 JavaMailSender mailSender, 
                                 TemplateEngine templateEngine,
                                 TemplateConfig templateConfig) {
        this.logger = loggerFactory.getLogger(EmailNotificationSender.class);
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.templateConfig = templateConfig;
    }
    
    @Override
    public NotificationResult send(NotificationRequest request) {
        logger.with("operation", "sendEmail")
                .with("notificationId", request.getId())
                .with("recipient", request.getRecipient())
                .with("notificationType", request.getType())
                .debug("Sending email notification");
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, charset);
            
            helper.setFrom(fromEmail);
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            
            // Check if we have a template for this notification type
            String templateName = getTemplateName(request);
            if (templateName != null) {
                Context context = new Context();
                context.setVariables(request.getMetadata());
                String htmlContent = templateEngine.process(templateName, context);
                helper.setText(htmlContent, true);
                
                logger.with("operation", "sendEmail")
                        .with("template", templateName)
                        .debug("Using HTML template for email");
            } else {
                // Plain text email
                helper.setText(request.getMessage(), false);
                
                logger.with("operation", "sendEmail")
                        .debug("Using plain text for email");
            }
            
            mailSender.send(message);
            
            // Create success result with email message ID
            String messageId = message.getMessageID() != null ? message.getMessageID() : UUID.randomUUID().toString();
            
            logger.with("operation", "sendEmail")
                    .with("notificationId", request.getId())
                    .with("messageId", messageId)
                    .info("Email sent successfully");
            
            return NotificationResult.success(
                request.getId(),
                "Email sent successfully",
                messageId
            );
            
        } catch (MessagingException e) {
            logger.with("operation", "sendEmail")
                    .with("notificationId", request.getId())
                    .with("recipient", request.getRecipient())
                    .with("error", e.getMessage())
                    .error("Failed to send email notification", e);
            
            return NotificationResult.failure(
                request.getId(),
                "Failed to send email",
                e.getMessage()
            );
        }
    }
    
    @Override
    public boolean supportsChannel(String channelName) {
        return NotificationChannel.EMAIL.name().equals(channelName);
    }
    
    private String getTemplateName(NotificationRequest request) {
        // Get template name from configuration
        String templateName = templateConfig.getTemplateForType(request.getType().name());
        return templateName != null ? templateName : null;
    }
}