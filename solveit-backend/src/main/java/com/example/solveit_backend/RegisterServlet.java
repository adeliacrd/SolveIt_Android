package com.example.solveit_backend;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // --- Leitura dos novos parâmetros ---
        String fullName = request.getParameter("fullName");
        String cpf = request.getParameter("cpf");
        String email = request.getParameter("email");
        String companyName = request.getParameter("companyName");
        String loginSugerido = request.getParameter("loginSugerido");
        String password = request.getParameter("password");
        // A variável 'area' é sempre nula, o que está correto
        String area = null;

        // ✨ CORREÇÃO 1: Adicionada a verificação para 'loginSugerido' ✨
        if (fullName == null || cpf == null || email == null || password == null || companyName == null || loginSugerido == null) {
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

            // --- 1. PASSO 1: INSERIR EMPRESA (Busca/Insere) ---
            // ✨ CORREÇÃO 2: Removido o 'tamanho_empresa' que não existe mais ✨
            String sqlSelectId = "SELECT id_empresa FROM Empresa WHERE nome_empresa = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelectId);
            pstmtSelect.setString(1, companyName);
            ResultSet rsSelect = pstmtSelect.executeQuery();

            if (rsSelect.next()) {
                idEmpresaGerado = rsSelect.getInt("id_empresa");
            } else {
                String sqlInsertEmpresa = "INSERT INTO Empresa (nome_empresa) VALUES (?)";
                PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertEmpresa, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmtInsert.setString(1, companyName);
                pstmtInsert.executeUpdate();

                try (ResultSet rsGenerated = pstmtInsert.getGeneratedKeys()) {
                    if (rsGenerated.next()) {
                        idEmpresaGerado = rsGenerated.getInt(1);
                    }
                }
            }

            // --- 2. PASSO 2: INSERIR USUÁRIO ---
            if (idEmpresaGerado <= 0) {
                throw new SQLException("Erro: Não foi possível obter o ID da Empresa.");
            }

            // ✨ CORREÇÃO 3: Query SQL do usuário totalmente reconstruída ✨
            // A query anterior estava com colunas e valores fora de ordem e faltando o login_sugerido.
            String sqlUsuario = "INSERT INTO Usuarios (nome_usuario, cpf, email, login_sugerido, senha_hash, id_tipo_acesso, id_empresa, area, dt_cadastro, st_usuario) VALUES (?, ?, ?, ?, ?, 1, ?, NULL, GETDATE(), 1)";

            PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario);

            // ✨ CORREÇÃO 4: Ordem dos parâmetros corrigida para bater com a nova query ✨
            pstmtUsuario.setString(1, fullName);
            pstmtUsuario.setString(2, cpf);
            pstmtUsuario.setString(3, email);
            pstmtUsuario.setString(4, loginSugerido); // Estava faltando
            pstmtUsuario.setString(5, password);      // Estava na posição errada
            pstmtUsuario.setInt(6, idEmpresaGerado);

            int linhasAfetadas = pstmtUsuario.executeUpdate();

            if (linhasAfetadas > 0) {
                conn.commit();
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(new RegisterResponse(true, "Usuário registrado com sucesso!")));
            } else {
                conn.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new RegisterResponse(false, "Falha ao inserir usuário no banco de dados.")));
            }
            return; // Adicionado para garantir que o código pare aqui

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }

            System.err.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();

            // ✨ CORREÇÃO 5: Mensagem de erro mais específica para o usuário ✨
            if (e.getMessage().toLowerCase().contains("unique constraint") || (e.getSQLState() != null && e.getSQLState().startsWith("23"))) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // Erro 409
                String erroMsg = "Erro: O E-mail ou CPF informado já está em uso.";
                if (e.getMessage().toLowerCase().contains("cpf")) {
                    erroMsg = "Erro: O CPF informado já está em uso.";
                } else if (e.getMessage().toLowerCase().contains("email")) {
                    erroMsg = "Erro: O E-mail informado já está em uso.";
                }
                response.getWriter().write(gson.toJson(new RegisterResponse(false, erroMsg)));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new RegisterResponse(false, "Erro interno de SQL: " + e.getMessage())));
            }
            return;

        } catch (Exception e) {
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