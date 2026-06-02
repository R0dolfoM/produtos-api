package com.ada.resources;

import com.ada.model.Usuario;
import com.ada.view.LoginRequestDTO;
import com.ada.view.RegisterRequestDTO;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.Set;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/register")
    @PermitAll
    @Transactional
    public Response register(RegisterRequestDTO dto) {

        Usuario usuarioExistente =
                Usuario.find("email", dto.email())
                        .firstResult();

        if (usuarioExistente != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(
                            java.util.Map.of(
                                    "erro",
                                    "E-mail já cadastrado"
                            )
                    )
                    .build();
        }

        Usuario usuario = new Usuario();

        usuario.nome = dto.nome();
        usuario.email = dto.email();
        usuario.senha =
                BcryptUtil.bcryptHash(dto.senha());

        usuario.role =
                dto.role() == null
                        ? "USER"
                        : dto.role();

        usuario.persist();

        return Response.status(Response.Status.CREATED)
                .entity(
                        java.util.Map.of(
                                "id", usuario.id,
                                "nome", usuario.nome,
                                "email", usuario.email,
                                "role", usuario.role
                        )
                )
                .build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequestDTO dto) {

        Usuario usuario =
                Usuario.find("email", dto.email())
                        .firstResult();

        if (usuario == null ||
                !BcryptUtil.matches(
                        dto.senha(),
                        usuario.senha)) {

            return Response.status(
                            Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        String token =
                Jwt.issuer("produtos-api")
                        .subject(usuario.email)
                        .groups(Set.of(usuario.role))
                        .expiresIn(Duration.ofHours(1))
                        .sign();

        return Response.ok(
                java.util.Map.of(
                        "token", token
                )
        ).build();
    }

    @GET
    @Path("/teste")
    @PermitAll
    public String teste() {
        return "AUTH OK";
    }
}