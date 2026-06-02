package com.ada.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class EstoqueProducer {

    @Inject
    @Channel("estoque-out")
    Emitter<String> emitter;

    public void enviarAlerta(String mensagem) {
        emitter.send(mensagem);
    }
}
