package com.ada.resources;

import com.ada.model.Usuario;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @GET
    public List<Usuario> listar() {
        return Usuario.listAll();
    }
}