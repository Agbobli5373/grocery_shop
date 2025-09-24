package com.groceryshop.sse;

import java.time.LocalDateTime;

public class SseEvent {
    private String id;
    private String eventType;
    private Object data;
    private LocalDateTime timestamp;

    public SseEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public SseEvent(String eventType, Object data) {
        this();
        this.eventType = eventType;
        this.data = data;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public SseEvent(String id, String eventType, Object data) {
        this(eventType, data);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
