package com.ada.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EstoqueConsumer {

    @Incoming("estoque-in")
    public void receberMensagem(String mensagem) {

        System.out.println(
                "⚠️ ALERTA KAFKA: "
                        + mensagem
        );
    }
}