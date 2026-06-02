package com.ada.view;

public record RegisterRequestDTO(
        String nome,
        String email,
        String senha,
        String role
) {
}
