package br.com.cotiinformatica.api_usuarios.dtos;

/*
 Objeto que representa os dados que a API deverá
 receber para cadastrar um novo usuário.
*/
public record CriarUsuarioRequest(

        String nome, // Nome do usuário a ser criado
        String email, // Email do usuário a ser criado
        String senha // Senha do usuário
) {
}
