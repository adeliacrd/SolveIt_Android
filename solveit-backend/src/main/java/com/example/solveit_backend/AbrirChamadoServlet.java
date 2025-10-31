package com.example.solveit_backend;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AbrirChamadoServlet extends HttpServlet {

    // Classe interna para a resposta JSON (sem alterações)
    private static class AbrirChamadoResponse {
        boolean success;
        String message;
        Integer id_chamado;

        public AbrirChamadoResponse(boolean success, String message, Integer id_chamado) {
            this.success = success;
            this.message = message;
            this.id_chamado = id_chamado;
        }
        public AbrirChamadoResponse(boolean success, String message) {
            this(success, message, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // --- 1. Leitura dos Parâmetros (sem alterações) ---
        String titulo = request.getParameter("titulo");
        String idUsuarioAberturaStr = request.getParameter("id_usuario_abertura");
        String prioridadeTexto = request.getParameter("prioridade");
        String idCategoriaStr = request.getParameter("id_categoria");
        String email = request.getParameter("email");
        String descricao = request.getParameter("descricao");

        // --- 2. Validação dos Parâmetros (sem alterações) ---
        if (titulo == null || titulo.trim().isEmpty() ||
                idUsuarioAberturaStr == null || idUsuarioAberturaStr.trim().isEmpty() ||
                prioridadeTexto == null || prioridadeTexto.equals("Selecione") ||
                idCategoriaStr == null || idCategoriaStr.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Todos os campos marcados com * são obrigatórios.")));
            return;
        }

        // --- 3. Lógica de "Tradução" da Prioridade (sem alterações) ---
        int idCategoria = -1;
        int idUsuarioAbertura = -1;
        int idPrioridade = -1;
        switch (prioridadeTexto) {
            case "Urgente": idPrioridade = 1; break;
            case "Alta": idPrioridade = 2; break;
            case "Média": idPrioridade = 3; break;
            case "Baixa": idPrioridade = 4; break;
        }

        try {
            idCategoria = Integer.parseInt(idCategoriaStr);
            idUsuarioAbertura = Integer.parseInt(idUsuarioAberturaStr);
            if (idCategoria <= 0 || idUsuarioAbertura <= 0 || idPrioridade <= 0) {
                throw new NumberFormatException("IDs devem ser positivos.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Um dos IDs selecionados (categoria, usuário ou prioridade) é inválido.")));
            return;
        }

        // --- 4. Lógica de Inserção no Banco (COM A COLUNA E VALOR CORRIGIDOS) ---

        // ✨ CORREÇÃO AQUI: 'st_chamado' -> 'id_status' E 'Novo' -> 1 ✨
        String sql = "INSERT INTO Chamados (titulo, id_usuario, id_prioridade, email_contato, desc_chamado, id_categoria, dt_abertura, id_status) VALUES (?, ?, ?, ?, ?, ?, GETDATE(), 1)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int idChamadoGerado = -1;

        try {
            conn = DriverManager.getConnection(
                    DatabaseConfig.getDbUrl(),
                    DatabaseConfig.getDbUsername(),
                    DatabaseConfig.getDbPassword()
            );
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Define os parâmetros (a ordem não muda, pois os últimos 2 valores são fixos)
            pstmt.setString(1, titulo);
            pstmt.setInt(2, idUsuarioAbertura);
            pstmt.setInt(3, idPrioridade);
            pstmt.setString(4, email);
            pstmt.setString(5, descricao);
            pstmt.setInt(6, idCategoria);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) { idChamadoGerado = generatedKeys.getInt(1); }
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(new AbrirChamadoResponse(true, "Chamado aberto com sucesso!", idChamadoGerado)));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Falha ao inserir chamado no banco de dados (0 linhas afetadas).")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Erro de SQL ao abrir chamado: " + e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Erro inesperado no servidor ao abrir chamado.")));
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException ignore) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignore) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignore) {}
        }
    }
}