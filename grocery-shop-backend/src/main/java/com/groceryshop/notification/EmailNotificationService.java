package com.groceryshop.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 * In a production environment, this would integrate with an email service provider
 * like SendGrid, AWS SES, or Mailgun.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final NotificationQueue notificationQueue;

    public EmailNotificationService(NotificationQueue notificationQueue) {
        this.notificationQueue = notificationQueue;
    }

    /**
     * Sends an email using the provided template.
     *
     * @param to recipient email address
     * @param template the email template
     */
    public void sendEmail(String to, EmailTemplate template) {
        EmailTemplate.ResolvedEmail resolved = template.resolve();

        NotificationQueue.NotificationTask task = new NotificationQueue.NotificationTask(
            NotificationQueue.NotificationType.EMAIL,
            to,
            resolved.subject(),
            resolved.body(),
            template
        );

        notificationQueue.enqueue(task);
        log.info("Email notification queued for {}: {}", to, resolved.subject());
    }

    /**
     * Sends a plain text email.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     */
    public void sendEmail(String to, String subject, String body) {
        NotificationQueue.NotificationTask task = new NotificationQueue.NotificationTask(
            NotificationQueue.NotificationType.EMAIL,
            to,
            subject,
            body,
            null
        );

        notificationQueue.enqueue(task);
        log.info("Plain text email notification queued for {}: {}", to, subject);
    }

    /**
     * Processes queued email notifications.
     * This method would typically be called by a background worker or scheduled task.
     */
    public void processQueuedEmails() {
        while (!notificationQueue.isEmpty()) {
            try {
                NotificationQueue.NotificationTask task = notificationQueue.dequeue();

                if (task.type() == NotificationQueue.NotificationType.EMAIL) {
                    sendEmailViaProvider(task.recipient(), task.subject(), task.content());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Email processing interrupted", e);
                break;
            } catch (Exception e) {
                log.error("Failed to process email notification", e);
            }
        }
    }

    /**
     * Sends email via external email service provider.
     * This is a placeholder implementation that would be replaced with actual
     * email service integration (SendGrid, AWS SES, etc.)
     *
     * @param to recipient email
     * @param subject email subject
     * @param body email body
     */
    private void sendEmailViaProvider(String to, String subject, String body) {
        // Placeholder implementation
        // In production, this would integrate with:
        // - SendGrid API
        // - AWS SES
        // - Mailgun
        // - SMTP server
        // - etc.

        log.info("Sending email to {} with subject: {}", to, subject);
        log.debug("Email body: {}", body);

        // Simulate email sending delay
        try {
            Thread.sleep(100); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Email sent successfully to {}", to);
    }
}
