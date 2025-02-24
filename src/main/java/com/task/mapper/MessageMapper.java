package com.task.mapper;

import com.task.domain.message.GroupMessage;
import com.task.domain.message.Message;
import com.task.domain.message.UserMessage;
import com.task.domain.Group;
import com.task.domain.User;
import com.task.dto.MessageDTO;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageMapper {

    public Uni<Message> toMessage(MessageDTO dto) {
        if (checkForBaseExistance(dto)) {
            return Uni.createFrom().nullItem();
        }
        Message message = new Message();
        message.setMessage(dto.getMessage());
        return findUserByUsername(dto.getSenderUsername())
                .onItem()
                .ifNotNull()
                .transform(user -> {
                    message.setSender(user);
                    return message;
                })
                .onItem()
                .ifNull()
                .continueWith(() -> null);
    }

    public Uni<UserMessage> toUserMessage(MessageDTO dto) {
        if (checkForBaseExistance(dto) || dto.getReceiverUsername() == null || dto.getGroupName() != null) {
            return Uni.createFrom().nullItem();
        }
        Uni<User> sender = findUserByUsername(dto.getSenderUsername());
        Uni<User> receiver = findUserByUsername(dto.getReceiverUsername());
        return Uni.combine().all().unis(sender, receiver)
                .asTuple()
                .flatMap(
                        sides -> {
                            User senderSide = sides.getItem1();
                            User receiverSide = sides.getItem2();
                            if (senderSide == null || receiverSide == null) {
                                return Uni.createFrom().nullItem();
                            }
                            UserMessage userMessage = new UserMessage(senderSide, dto.getMessage(), receiverSide);
                            return Uni.createFrom().item(userMessage);
                        });
    }

    public Uni<GroupMessage> toGroupMassage(MessageDTO dto) {
        if (checkForBaseExistance(dto) || dto.getReceiverUsername() != null || dto.getGroupName() == null) {
            return Uni.createFrom().nullItem();
        }
        Uni<User> sender = findUserByUsername(dto.getSenderUsername());
        Uni<Group> group = Group.find("name", dto.getGroupName()).firstResult();
        return Uni.combine().all().unis(sender, group)
                .asTuple()
                .flatMap(sides -> {
                    User senderSide = sides.getItem1();
                    Group receiverSide = sides.getItem2();
                    if (senderSide == null || receiverSide == null) {
                        return Uni.createFrom().nullItem();
                    }
                    GroupMessage message = new GroupMessage(senderSide, dto.getMessage(), receiverSide);
                    return Uni.createFrom().item(message);
                });
    }

    private Uni<User> findUserByUsername(String username) {
        return username == null ? Uni.createFrom().nullItem() : User.find("username", username).firstResult();
    }

    public boolean checkForBaseExistance(MessageDTO dto) {
        return dto == null || dto.getMessage() == null || dto.getSenderUsername() == null;
    }
}
