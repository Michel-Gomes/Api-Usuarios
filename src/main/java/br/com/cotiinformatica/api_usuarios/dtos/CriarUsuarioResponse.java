package br.com.cotiinformatica.api_usuarios.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 Objeto que representa os dados que a API deverá
 retornar após cadastrar um novo usuário.
*/
public record CriarUsuarioResponse(

        UUID id,
        String nome,
        String email,
        LocalDateTime dataHoraCriacao
) {
}
