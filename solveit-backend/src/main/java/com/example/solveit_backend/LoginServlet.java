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

        private static class LoginResponse {
            boolean success;
            String message;
            Integer id_acesso;

            public LoginResponse(boolean success, String message, Integer id_acesso) {
                this.success = success;
                this.message = message;
                this.id_acesso = id_acesso;
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
                response.getWriter().write(gson.toJson(new LoginResponse(false, "E-mail e senha são obrigatórios.", null)));
                return;
            }

            // 2. Conectar ao SQL Server e verificar
            try (Connection conn = DriverManager.getConnection(
                    DatabaseConfig.getDbUrl(),
                    DatabaseConfig.getDbUsername(),
                    DatabaseConfig.getDbPassword()
            )) {

                String sql = "SELECT senha_hash, id_tipo_acesso FROM USUARIOS WHERE email = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String senhaDoBanco = rs.getString("senha_hash");
                    int idAcesso = rs.getInt("id_tipo_acesso");

                    if (senha.equals(senhaDoBanco)) { // Validação de senha de teste
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write(gson.toJson(new LoginResponse(true, "Login bem-sucedido!", idAcesso)));
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new LoginResponse(false, "Erro interno do servidor: " + e.getMessage(), null)));
                return;
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new LoginResponse(false, "Credenciais inválidas. Verifique e-mail e senha.", null)));
        }
    }