package com.task.resources;

import com.task.domain.User;
import com.task.dto.LoginDTO;
import com.task.security.TokenService;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jose4j.lang.JoseException;

@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    TokenService tokenService;

    @POST
    @Path("/registration")
    @WithTransaction
    public Uni<String> createUser(User user) {
        user.setPassword(BcryptUtil.bcryptHash(user.getPassword()));
        return user.persist()
                .onItem()
                .ifNotNull()
                .transform(Unchecked.function(persistedUser -> {
                    try {
                        return tokenService.generateTokenString(user.getUsername());
                    } catch (JoseException ex) {
                        throw new WebApplicationException(404);
                    } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                    }
                }))
                .onItem().ifNull().failWith(() -> new WebApplicationException("Could not create user"));
    }

    @GET
    @Path("/login")
    public Uni<String> login(@Valid LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        return User.find("username", username)
                .singleResult()
                .onItem()
                .ifNotNull()
                .transform(Unchecked.function(user -> {
                    if (BcryptUtil.matches(password, ((User) user).getPassword())) {
                        return tokenService.generateTokenString(username);
                    }
                    throw new UnauthorizedException("Invalid username or password");
                }))
                .onItem().ifNull().failWith(() -> new UnauthorizedException("Invalid username or password"));
    }
}
