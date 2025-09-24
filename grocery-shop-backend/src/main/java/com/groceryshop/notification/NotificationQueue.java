package com.groceryshop.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In-memory notification queue for processing notifications asynchronously.
 * In a production environment, this would be replaced with a persistent queue
 * like RabbitMQ or database-backed queue.
 */
@Component
public class NotificationQueue {

    private static final Logger log = LoggerFactory.getLogger(NotificationQueue.class);

    private final BlockingQueue<NotificationTask> queue = new LinkedBlockingQueue<>();

    /**
     * Adds a notification task to the queue.
     *
     * @param task the notification task to enqueue
     */
    public void enqueue(NotificationTask task) {
        try {
            queue.put(task);
            log.debug("Enqueued notification task: {}", task.type());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to enqueue notification task", e);
        }
    }

    /**
     * Retrieves and removes the head of the queue, waiting if necessary.
     *
     * @return the head of the queue
     * @throws InterruptedException if interrupted while waiting
     */
    public NotificationTask dequeue() throws InterruptedException {
        return queue.take();
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Notification task record.
     */
    public record NotificationTask(
        NotificationType type,
        String recipient,
        String subject,
        String content,
        Object metadata
    ) {}

    /**
     * Types of notifications.
     */
    public enum NotificationType {
        EMAIL,
        PUSH,
        SMS
    }
}
