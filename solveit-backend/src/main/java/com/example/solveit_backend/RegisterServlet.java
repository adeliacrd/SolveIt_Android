package com.example.solveit_backend;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // <--- AGORA CORRETO
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    private static class RegisterResponse {
        boolean success;
        String message;
        public RegisterResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String companyName = request.getParameter("companyName");
        String companySize = request.getParameter("companySize");
        String area = null;

        if (fullName == null || email == null || password == null || companyName == null || companySize == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new RegisterResponse(false, "Todos os campos são obrigatórios.")));
            return;
        }

        Connection conn = null;
        int idEmpresaGerado = -1;

        try {
            conn = DriverManager.getConnection(
                    DatabaseConfig.getDbUrl(),
                    DatabaseConfig.getDbUsername(),
                    DatabaseConfig.getDbPassword()
            );
            conn.setAutoCommit(false); // INÍCIO DA TRANSAÇÃO

            // 1. PASSO 1: INSERIR EMPRESA (Busca/Insere)
            String sqlSelectId = "SELECT id_empresa FROM Empresa WHERE nome_empresa = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelectId);
            pstmtSelect.setString(1, companyName);
            ResultSet rsSelect = pstmtSelect.executeQuery();

            if (rsSelect.next()) {
                idEmpresaGerado = rsSelect.getInt("id_empresa");
            } else {
                String sqlInsertEmpresa = "INSERT INTO Empresa (nome_empresa, tamanho_empresa) VALUES (?, ?)";
                PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertEmpresa, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmtInsert.setString(1, companyName);
                pstmtInsert.setString(2, companySize);

                pstmtInsert.executeUpdate();

                try (ResultSet rsGenerated = pstmtInsert.getGeneratedKeys()) {
                    if (rsGenerated.next()) {
                        idEmpresaGerado = rsGenerated.getInt(1);
                    }
                }
            }

            // 2. PASSO 2: INSERIR USUÁRIO
            if (idEmpresaGerado <= 0) {
                throw new SQLException("Erro: Não foi possível obter o ID da Empresa.");
            }

            String sqlUsuario = "INSERT INTO Usuarios (nome_usuario, email, senha_hash, id_tipo_acesso, id_empresa, area, dt_cadastro, st_usuario) VALUES (?, ?, ?, 1, ?, ?, GETDATE(), 1)";
            PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario);
            pstmtUsuario.setString(1, fullName);
            pstmtUsuario.setString(2, email);
            pstmtUsuario.setString(3, password);
            pstmtUsuario.setInt(4, idEmpresaGerado);
            pstmtUsuario.setString(5, area);

            int linhasAfetadas = pstmtUsuario.executeUpdate();

            if (linhasAfetadas > 0) {
                conn.commit();
                response.setStatus(HttpServletResponse.SC_CREATED);
                String msg = (idEmpresaGerado > 0) ? "Usuário registrado com sucesso!" : "Empresa e Usuário registrados com sucesso!";
                response.getWriter().write(gson.toJson(new RegisterResponse(true, msg)));
                return;
            } else {
                conn.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new RegisterResponse(false, "Falha ao inserir usuário no banco de dados.")));
                return;
            }

        } catch (SQLException e) {
            // Tratamento de erros de SQL e e-mail duplicado
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }

            // Imprime o erro real no console do back-end
            System.err.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();

            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(gson.toJson(new RegisterResponse(false, "Erro: E-mail ou Nome de Empresa já está em uso.")));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new RegisterResponse(false, "Erro interno de SQL: " + e.getMessage())));
            }
            return;

        } catch (Exception e) {
            // Erro de runtime ou conexão
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new RegisterResponse(false, "Erro inesperado: " + e.getMessage())));
            return;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignore) {}
            }
        }
    }
}