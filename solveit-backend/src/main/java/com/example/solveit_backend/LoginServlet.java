package com.example.solveit_backend;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    // ✨ CORREÇÃO 1: Adicionar campo nome_usuario na classe de resposta ✨
    private static class LoginResponse {
        boolean success;
        String message;
        Integer id_acesso;
        Integer id_tipo_acesso;
        String nome_usuario; // ✨ NOVO CAMPO ✨

        // ✨ CORREÇÃO 2: Atualizar o construtor para receber o nome ✨
        public LoginResponse(boolean success, String message, Integer id_acesso, Integer id_tipo_acesso, String nome_usuario) {
            this.success = success;
            this.message = message;
            this.id_acesso = id_acesso;
            this.id_tipo_acesso = id_tipo_acesso;
            this.nome_usuario = nome_usuario; // ✨ Atribui o nome ✨
        }

        // ✨ Construtor para respostas de erro (sem nome) ✨
        public LoginResponse(boolean success, String message) {
            this(success, message, null, null, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || senha == null || email.trim().isEmpty() || senha.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // ✨ Usa o construtor de erro ✨
            response.getWriter().write(gson.toJson(new LoginResponse(false, "E-mail e senha são obrigatórios.")));
            return;
        }

        // 2. Conectar ao SQL Server e verificar
        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(),
                DatabaseConfig.getDbUsername(),
                DatabaseConfig.getDbPassword()
        )) {

            // ✨ CORREÇÃO 3: Adicionar nome_usuario à query SQL ✨
            String sql = "SELECT id_usuario, senha_hash, id_tipo_acesso, nome_usuario FROM USUARIOS WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idUsuario = rs.getInt("id_usuario"); // ✨ Pegar o id_usuario também ✨
                String senhaDoBanco = rs.getString("senha_hash");
                int idAcesso = rs.getInt("id_tipo_acesso");
                String nomeDoBanco = rs.getString("nome_usuario"); // ✨ Pegar o nome_usuario ✨

                // Validação de senha (ainda simples, lembre-se de melhorar com hash no futuro)
                if (senha.equals(senhaDoBanco)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    // ✨ CORREÇÃO 4: Enviar o nome e o ID do usuário na resposta de sucesso ✨
                    // (Usando idUsuario como id_acesso para o app, ajuste se necessário)
                    response.getWriter().write(gson.toJson(new LoginResponse(true, "Login bem-sucedido!", idUsuario, idAcesso, nomeDoBanco)));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Loga o erro no console
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // ✨ Usa o construtor de erro ✨
            response.getWriter().write(gson.toJson(new LoginResponse(false, "Erro interno do servidor: " + e.getMessage())));
            return;
        }

        // Se chegou aqui, as credenciais estavam inválidas
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // ✨ Usa o construtor de erro ✨
        response.getWriter().write(gson.toJson(new LoginResponse(false, "Credenciais inválidas. Verifique e-mail e senha.")));
    }
}