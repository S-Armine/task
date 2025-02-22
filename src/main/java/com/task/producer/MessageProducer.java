package com.task.producer;

import com.task.dto.MessageDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class MessageProducer {

    @Inject
    @Channel("message")
    Emitter<MessageDTO> emitter;

    public void sendToMessages(MessageDTO message) {
        if (message != null) {
            emitter.send(message);
        }
    }
}
