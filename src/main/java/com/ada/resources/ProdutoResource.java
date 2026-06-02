package com.ada.resources;

import com.ada.model.Produto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import com.ada.messaging.EstoqueProducer;
import jakarta.inject.Inject;

import java.util.List;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    EstoqueProducer estoqueProducer;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @CacheResult(cacheName = "produtos-cache")
    public List<Produto> listar() {
        return Produto.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @CacheResult(cacheName = "produto-por-id")
    public Response buscarPorId(
            @PathParam("id") Long id) {

        Produto produto = Produto.findById(id);

        if (produto == null) {
            return Response.status(
                            Response.Status.NOT_FOUND)
                    .build();
        }

        return Response.ok(produto).build();
    }

    @POST
    @Transactional
    @RolesAllowed("ADMIN")
    @CacheInvalidateAll(cacheName = "produtos-cache")
    public Response criar(Produto produto) {

        produto.persist();

        return Response.status(Response.Status.CREATED)
                .entity(produto)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed("ADMIN")
    @CacheInvalidateAll(cacheName = "produtos-cache")
    public Response atualizar(@PathParam("id") Long id, Produto produtoAtualizado) {

        Produto produto = Produto.findById(id);

        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        produto.nome = produtoAtualizado.nome;
        produto.descricao = produtoAtualizado.descricao;
        produto.preco = produtoAtualizado.preco;
        produto.estoque = produtoAtualizado.estoque;

        if (produto.estoque < 5) {
            estoqueProducer.enviarAlerta(
                    "Produto " + produto.nome +
                            " está com estoque baixo: "
                            + produto.estoque
                            + " unidades"
            );
        }

        return Response.ok(produto).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed("ADMIN")
    @CacheInvalidateAll(cacheName = "produtos-cache")
    public Response deletar(@PathParam("id") Long id) {

        Produto produto = Produto.findById(id);

        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        produto.delete();

        return Response.noContent().build();
    }
}
