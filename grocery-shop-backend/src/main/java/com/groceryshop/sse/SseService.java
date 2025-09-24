package com.groceryshop.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final ConcurrentHashMap<String, SseEmitter> emitters;

    public SseService(ConcurrentHashMap<String, SseEmitter> emitters) {
        this.emitters = emitters;
    }

    public SseEmitter createEmitter(String emitterId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError((throwable) -> emitters.remove(emitterId));

        return emitter;
    }

    public void sendEvent(String emitterId, SseEvent event) {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name(event.getEventType())
                    .data(event.getData())
                    .id(event.getId())
                    .reconnectTime(3000L));
            } catch (IOException e) {
                emitters.remove(emitterId);
            }
        }
    }

    public void sendEventToAll(SseEvent event) {
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name(event.getEventType())
                    .data(event.getData())
                    .id(event.getId())
                    .reconnectTime(3000L));
            } catch (IOException e) {
                emitters.remove(id);
            }
        });
    }

    public void removeEmitter(String emitterId) {
        SseEmitter emitter = emitters.remove(emitterId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    public boolean hasEmitter(String emitterId) {
        return emitters.containsKey(emitterId);
    }
}
