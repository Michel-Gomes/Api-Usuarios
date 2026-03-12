package br.com.cotiinformatica.api_usuarios.services;

import br.com.cotiinformatica.api_usuarios.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.dtos.AutenticarUsuarioResponse;
import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.api_usuarios.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.api_usuarios.entities.Usuario;
import br.com.cotiinformatica.api_usuarios.repositories.UsuariosRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    /*
    Método para desenvolver o serviço de criação de usuário
     */


    /*
    Atributos
     */
    @Autowired// <------ Auto Inicialização do repositório
    private UsuariosRepository usuariosRepository;

    public CriarUsuarioResponse criarUsuario (CriarUsuarioRequest request){

        //Executar as validações
        validarNome(request.nome());
        validarEmail(request.email());
        validarEmailExistente(request.email());
        validarSenha(request.senha());

        //Criando um novo usuário
        var usuario = new Usuario();

        usuario.setNome(request.nome());
        // capturando o nome recebido
        usuario.setEmail(request.email());
        // capturando o email recebido
        usuario.setSenha(criptografarSenha(request.senha()));
        // capturando a senha recebida
        usuario.setDataCriacao(new Date());
        // gerando a data e hora atual

        //Salvar no banco da dados
        usuariosRepository.save(usuario);

        //retorna os dados da resposta
        return new CriarUsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                LocalDateTime.now()
        );
    }

     /*
    Método para desenvolver o serviço de autenticação de usuário
     */

    public AutenticarUsuarioResponse autenticarUsuario(AutenticarUsuarioRequest request) {

        //validar o email e senha recebidos no request
        validarEmail(request.email());
        validarSenha(request.senha());

        //Consultar o usuário no banco de dados através do email e senha
        var usuario = usuariosRepository.findByEmailAndSenha(request.email(), criptografarSenha(request.senha()));

        //verifiqcar se o usuário não foi encontrado
        if(usuario.isEmpty()) {
            throw new SecurityException(" Acesso negado. Usuário inválido.");
        }

        // retornar os dados do usuário
        var u = usuario.get();

        return new AutenticarUsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                LocalDateTime.now(),
                generateToken(u.getEmail()) //Gerando o Token JWT
        );
    }

    /*
    Validações
     */

    private void validarNome(String nome){
        if(nome == null || nome.trim().length() < 8){
            throw new IllegalArgumentException("O nome do usuário deve conter pelo menos 8 caracteres.");
        }
    }

    private void validarEmail(String email) {
        var regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || !Pattern.matches(regex, email)) {
            throw new IllegalArgumentException("O endereço de email informado é inválido.");
        }
    }
    private void validarEmailExistente(String email) {
        var usuario = usuariosRepository.findByEmail(email);
        if(usuario.isPresent()){
            throw new IllegalArgumentException("O email informado já está cadastrado para outro usuário.");
        }
    }

    private void validarSenha(String senha){
        var regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        if(senha == null || !Pattern.matches(regex, senha)){
                throw new IllegalArgumentException("A senha deve conter pelo menos 8 caracteres, " +
                        "incluindo letras maiúsculas, minúsculas, números e caracteres especiais.");
        }
    }
    /*
    Criptografia da Senha
     */
    private String criptografarSenha(String senha){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte [] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar a senha com SHA-256", e);
        }
    }

    /*
    Método para gerar os TOKENS JWT
     */
    public String generateToken(String email) {

        // CHAVE PARA CRIPTOGRAFAR O TOKEN (ASSINATURA DO TOKEN)
        var secretkey = "9c5efd32-1ee8-4396-bb21-4804ef30a2ba";

        return Jwts.builder()
                .setSubject(email) //identificação do usuário
                .setIssuedAt(new Date()) //data de geração do token
                .signWith(SignatureAlgorithm.HS256, secretkey)//Chave para assinatura
                .compact(); //Gerando o token
    }
}
