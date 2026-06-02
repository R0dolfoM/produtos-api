package com.ada;

import com.ada.model.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataLoader {

    @Transactional
    void onStart(@Observes StartupEvent ev) {

        if (Usuario.count() == 0) {

            Usuario admin = new Usuario();
            admin.nome = "Admin Sistema";
            admin.email = "admin@loja.com";
            admin.senha = BcryptUtil.bcryptHash("admin123");
            admin.role = "ADMIN";
            admin.persist();

            Usuario user = new Usuario();
            user.nome = "User Padrão";
            user.email = "user@loja.com";
            user.senha = BcryptUtil.bcryptHash("user123");
            user.role = "USER";
            user.persist();
        }
    }
}
