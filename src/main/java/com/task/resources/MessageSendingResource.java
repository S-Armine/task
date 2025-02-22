package com.task.resources;
import com.task.dto.MessageDTO;
import com.task.mapper.MessageMapper;
import com.task.producer.MessageProducer;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/message")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageSendingResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    MessageMapper messageMapper;

    @Inject
    MessageProducer messageProducer;

    @POST
    @Path("/all")
    @WithTransaction
    public Uni<Response> sendToAllUsers(@Valid MessageDTO messageDTO) {
        checkAuthorization(messageDTO);
        return messageMapper.toMessage(messageDTO)
                .onItem()
                .ifNotNull()
                .call(message -> message.persist())
                .onItem()
                .ifNotNull()
                .transform(message -> {
                    sendDTOToTopic(messageDTO);
                    return Response.status(201).build();
                })
                .onItem()
                .ifNull()
                .failWith(() -> new BadRequestException("Invalid message"));
    }

    @POST
    @Path("/group")
    @WithTransaction
    public Uni<Response> sendToGroup(@Valid MessageDTO messageDTO) {
        checkAuthorization(messageDTO);
        return messageMapper.toGroupMassage(messageDTO)
                .onItem()
                .ifNotNull()
                .call(message -> message.persist())
                .onItem()
                .ifNotNull()
                .transform(message -> {
                    sendDTOToTopic(messageDTO);
                    return Response.status(201).build();
                })
                .onItem()
                .ifNull()
                .failWith(() -> new BadRequestException("Invalid message"));
    }

    @POST
    @Path("/user")
    @WithTransaction
    public Uni<Response> sendToUser(@Valid MessageDTO messageDTO) {
        checkAuthorization(messageDTO);
        return messageMapper.toUserMessage(messageDTO)
                .onItem()
                .ifNull()
                .failWith(() -> new BadRequestException("Invalid message"))
                .onItem()
                .ifNotNull()
                .transform(message -> {
                    sendDTOToTopic(messageDTO);
                    return Response.status(201).build();
                });
    }

    private void sendDTOToTopic(MessageDTO messageDTO) {
        messageProducer.sendToMessages(messageDTO);
    }

    private void checkAuthorization(MessageDTO messageDTO) {
        if (isSenderNotAuthorized(messageDTO.getSenderUsername())) {
            System.out.println(securityContext.getUserPrincipal().getName());
            System.out.println(messageDTO.getSenderUsername());
            throw new UnauthorizedException("You are not authorized to make this request.");
        }
    }

    private boolean isSenderNotAuthorized(String userName) {
        return userName == null || !securityContext.getUserPrincipal().getName().equals(userName);
    }
}
