package com.groceryshop.notification;

import java.util.Map;

/**
 * Email template with placeholders for dynamic content.
 */
public record EmailTemplate(
    String subject,
    String body,
    Map<String, String> placeholders
) {

    /**
     * Creates a new EmailTemplate with the given subject and body.
     *
     * @param subject the email subject
     * @param body the email body with placeholders like ${name}, ${orderId}, etc.
     * @return a new EmailTemplate
     */
    public static EmailTemplate of(String subject, String body) {
        return new EmailTemplate(subject, body, Map.of());
    }

    /**
     * Creates a new EmailTemplate with placeholders.
     *
     * @param subject the email subject
     * @param body the email body with placeholders
     * @param placeholders the placeholder values
     * @return a new EmailTemplate
     */
    public static EmailTemplate of(String subject, String body, Map<String, String> placeholders) {
        return new EmailTemplate(subject, body, placeholders);
    }

    /**
     * Resolves placeholders in the template with actual values.
     *
     * @return the resolved email content
     */
    public ResolvedEmail resolve() {
        String resolvedSubject = resolvePlaceholders(subject);
        String resolvedBody = resolvePlaceholders(body);
        return new ResolvedEmail(resolvedSubject, resolvedBody);
    }

    private String resolvePlaceholders(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }

    /**
     * Resolved email content without placeholders.
     */
    public record ResolvedEmail(String subject, String body) {}
}
