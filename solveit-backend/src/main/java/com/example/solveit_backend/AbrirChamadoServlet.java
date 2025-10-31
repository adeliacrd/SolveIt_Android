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

import java.util.ArrayList;
import java.util.List;

public class AbrirChamadoServlet extends HttpServlet {

    /**
     * DTO (Molde) para a lista RESUMIDA de chamados (Tela Mestre).
     * Contém apenas os 4 campos para a lista do ADM.
     */
    private static class ChamadoDTO {
        int id_chamado;
        String titulo;
        String desc_prioridade;
        String desc_status;

        // Construtor usado no `doGet`
        public ChamadoDTO(int id_chamado, String titulo, String desc_prioridade, String desc_status) {
            this.id_chamado = id_chamado;
            this.titulo = titulo;
            this.desc_prioridade = desc_prioridade;
            this.desc_status = desc_status;
        }
    }

    /**
     * DTO (Molde) para a visão COMPLETA do chamado (Tela Detalhe).
     * Contém todos os campos necessários para a tela de "Informações".
     */
    private static class ChamadoCompletoDTO {
        // Campos (Gson pode acessar campos privados de classes internas)
        int id_chamado;
        String titulo;
        String desc_chamado;
        String dt_abertura;
        String dt_fechamento;
        String email_contato;
        String desc_prioridade;
        String desc_status;
        String desc_categoria;
        String nome_solicitante;
        String nome_agente;

        // Construtor usado no `doGet`
        public ChamadoCompletoDTO(int id_chamado, String titulo, String desc_chamado,
                                  String dt_abertura, String dt_fechamento, String email_contato,
                                  String desc_prioridade, String desc_status, String desc_categoria,
                                  String nome_solicitante, String nome_agente) {
            this.id_chamado = id_chamado;
            this.titulo = titulo;
            this.desc_chamado = desc_chamado;
            this.dt_abertura = dt_abertura;
            this.dt_fechamento = dt_fechamento;
            this.email_contato = email_contato;
            this.desc_prioridade = desc_prioridade;
            this.desc_status = desc_status;
            this.desc_categoria = desc_categoria;
            this.nome_solicitante = nome_solicitante;
            this.nome_agente = nome_agente;
        }
    }

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Tenta pegar o ID da URL (ex: /api/chamados?id_chamado=1)
        String idChamadoParam = request.getParameter("id_chamado");

        if (idChamadoParam == null || idChamadoParam.isEmpty()) {
            // --- CASO 1: NENHUM ID FOI PASSADO. RETORNA A LISTA SIMPLES. ---
            buscarListaDeChamados(response, gson);
        } else {
            // --- CASO 2: UM ID FOI PASSADO. RETORNA OS DETALHES DAQUELE CHAMADO. ---
            try {
                int idChamado = Integer.parseInt(idChamadoParam);
                buscarChamadoPorId(response, gson, idChamado);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"ID do chamado inválido.\"}");
            }
        }
    }

    /**
     * MÉTODO AUXILIAR 1: Busca a lista SIMPLES de todos os chamados (para a tela Mestre).
     */
    private void buscarListaDeChamados(HttpServletResponse response, Gson gson) throws IOException {
        List<ChamadoDTO> chamados = new ArrayList<>();

        // Query SIMPLES com JOINs para a lista
        // ✨ ATENÇÃO: Confirme se os nomes das tabelas 'Prioridade' e 'Status' estão corretos ✨
        String sql = "SELECT " +
                "  c.id_chamado, c.titulo, " +
                "  p.desc_prioridade, s.desc_status " +
                "FROM Chamados c " +
                "JOIN Prioridades p ON c.id_prioridade = p.niv_prioridade " +
                "JOIN StatusChamado s ON c.id_status = s.id_status " +
                "ORDER BY c.id_chamado DESC";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                chamados.add(new ChamadoDTO(
                        rs.getInt("id_chamado"),
                        rs.getString("titulo"),
                        rs.getString("desc_prioridade"),
                        rs.getString("desc_status")
                ));
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(chamados));

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro ao buscar lista de chamados: " + e.getMessage() + "\"}");
        }
    }

    /**
     * MÉTODO AUXILIAR 2: Busca todos os detalhes de UM chamado específico (para a tela Detalhe).
     */
    private void buscarChamadoPorId(HttpServletResponse response, Gson gson, int idChamado) throws IOException {

        // Query COMPLEXA com todos os JOINs
        // ✨ ATENÇÃO: Confirme os nomes das tabelas (Prioridade, Status, Categoria, Usuarios, atribuicao_chamado) ✨
        String sql = "SELECT " +
                "  c.id_chamado, c.titulo, c.desc_chamado, CONVERT(varchar, c.dt_abertura, 103) AS dt_abertura, " +
                "  CONVERT(varchar, c.dt_fechamento, 103) AS dt_fechamento, c.email_contato, " +
                "  p.desc_prioridade, " +
                "  s.desc_status, " +
                "  cat.desc_categoria, " +
                "  u_sol.nome_usuario AS nome_solicitante, " +
                "  u_agente.nome_usuario AS nome_agente " +
                "FROM Chamados c " +
                "JOIN Prioridade p ON c.id_prioridade = p.id_prioridade " +
                "JOIN Status s ON c.id_status = s.id_status " +
                "JOIN Categoria cat ON c.id_categoria = cat.id_categoria " +
                "JOIN Usuarios u_sol ON c.id_usuario = u_sol.id_usuario " + // Join para o solicitante
                "LEFT JOIN atribuicao_chamado atrib ON c.id_chamado = atrib.id_chamado " + // LEFT JOIN caso não haja agente
                "LEFT JOIN Usuarios u_agente ON atrib.id_usuario = u_agente.id_usuario " + // Join para o agente
                "WHERE c.id_chamado = ?";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idChamado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Monta o objeto DTO completo
                    ChamadoCompletoDTO chamado = new ChamadoCompletoDTO(
                            rs.getInt("id_chamado"),
                            rs.getString("titulo"),
                            rs.getString("desc_chamado"),
                            rs.getString("dt_abertura"),
                            rs.getString("dt_fechamento"),
                            rs.getString("email_contato"),
                            rs.getString("desc_prioridade"),
                            rs.getString("desc_status"),
                            rs.getString("desc_categoria"),
                            rs.getString("nome_solicitante"),
                            rs.getString("nome_agente")
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(chamado)); // Envia o objeto único
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Erro 404
                    response.getWriter().write("{\"success\": false, \"message\": \"Chamado não encontrado.\"}");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro ao buscar detalhes do chamado: " + e.getMessage() + "\"}");
        }
    }

} // Fim da classe AbrirChamadoServlet