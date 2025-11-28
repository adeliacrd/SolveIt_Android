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

    // ==========================================================
    // ✨ DTOs INTERNOS (CLASSES DE DADOS) ✨
    // ==========================================================

    /**
     * DTO para uma única interação/mensagem na Timeline.
     */
    private static class InteracaoDTO {
        private int id_interacoes;
        private int id_chamado;
        private int id_usuario;
        private String nome_usuario; // Nome de quem enviou
        private String mensagem;
        private String dt_interacao;

        public InteracaoDTO(int id_interacoes, int id_chamado, int id_usuario, String nome_usuario, String mensagem, String dt_interacao) {
            this.id_interacoes = id_interacoes;
            this.id_chamado = id_chamado;
            this.id_usuario = id_usuario;
            this.nome_usuario = nome_usuario;
            this.mensagem = mensagem;
            this.dt_interacao = dt_interacao;
        }
    }

    /**
     * DTO para a lista RESUMIDA de chamados (Tela Mestre/Listas).
     */
    private static class ChamadoDTO {
        int id_chamado; String titulo; String desc_prioridade; String desc_status;
        int id_usuario; Integer id_usuario_atribuido;
        String dt_atualizacao; String nota_avaliacao; String dt_avaliacao;
        String desc_chamado; String nome_solicitante;

        public ChamadoDTO(int id, String t, String p, String s, int u, Integer a, String dta, String nota, String dtv, String desc, String nome) {
            this.id_chamado = id; this.titulo = t; this.desc_prioridade = p; this.desc_status = s;
            this.id_usuario = u; this.id_usuario_atribuido = a;
            this.dt_atualizacao = dta; this.nota_avaliacao = nota; this.dt_avaliacao = dtv;
            this.desc_chamado = desc; this.nome_solicitante = nome;
        }
    }

    /**
     * DTO para a visão COMPLETA do chamado (Tela Detalhe).
     */
    private static class ChamadoCompletoDTO {
        int id_chamado; String titulo; String desc_chamado; String dt_abertura;
        String dt_fechamento; String email_contato; String desc_prioridade;
        String desc_status; String desc_categoria; String nome_solicitante; String nome_agente;
        List<InteracaoDTO> timeline; // Lista de mensagens

        public ChamadoCompletoDTO(int id, String t, String d, String da, String df, String e,
                                  String dp, String ds, String dc, String ns, String na,
                                  List<InteracaoDTO> tl) {
            id_chamado = id; titulo = t; desc_chamado = d; dt_abertura = da; dt_fechamento = df; email_contato = e;
            desc_prioridade = dp; desc_status = ds; desc_categoria = dc; nome_solicitante = ns; nome_agente = na;
            timeline = tl;
        }
    }

    // Resposta JSON padrão para o doPost
    private static class AbrirChamadoResponse {
        boolean success; String message; Integer id_chamado;
        public AbrirChamadoResponse(boolean s, String m, Integer id) { success = s; message = m; id_chamado = id; }
        public AbrirChamadoResponse(boolean s, String m) { this(s, m, null); }
    }

    // ==========================================================
    // MÉTODO doPost (CRIAR NOVO CHAMADO)
    // ==========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String titulo = request.getParameter("titulo");
        String idUsuarioAberturaStr = request.getParameter("id_usuario_abertura");
        String prioridadeTexto = request.getParameter("prioridade");
        String idCategoriaStr = request.getParameter("id_categoria");
        String email = request.getParameter("email");
        String descricao = request.getParameter("descricao");

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

        // INSERT inicial: id_status = 1 (Novo/Aberto), dt_atualizacao = dt_abertura
        String sql = "INSERT INTO Chamados (titulo, id_usuario, id_prioridade, email_contato, desc_chamado, id_categoria, dt_abertura, dt_atualizacao, id_status) VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), 1)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int idChamadoGerado = -1;

        try {
            conn = DriverManager.getConnection(
                    DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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
                response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Falha ao inserir chamado no banco de dados.")));
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

    // ==========================================================
    // MÉTODO doGet (BUSCAR LISTA OU DETALHE)
    // ==========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idChamadoParam = request.getParameter("id_chamado");

        if (idChamadoParam == null || idChamadoParam.isEmpty()) {
            // Se não tem ID, busca a lista
            buscarListaDeChamados(response, gson);
        } else {
            // Se tem ID, busca os detalhes
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

        // ✨ QUERY CORRIGIDA E ATUALIZADA COM OS NOVOS CAMPOS ✨
        String sql = "SELECT " +
                "  c.id_chamado, c.titulo, c.id_usuario, c.nota_avaliacao, c.desc_chamado, " +
                "  CONVERT(varchar(20), c.dt_avaliacao, 103) AS dt_avaliacao_txt, " +
                "  CONVERT(varchar(20), c.dt_atualizacao, 120) AS dt_atualizacao_txt, " +
                "  p.desc_prioridade, s.desc_status, " +
                "  atrib.id_usuario_atribuido, " + // Alias do LEFT JOIN
                "  u.nome_usuario AS nome_solicitante " +
                "FROM Chamados c " +
                "JOIN Prioridades p ON c.id_prioridade = p.niv_prioridade " + // Nome da tabela: Prioridades
                "JOIN StatusChamado s ON c.id_status = s.id_status " + // Nome da tabela: StatusChamado
                "JOIN Usuarios u ON c.id_usuario = u.id_usuario " + // JOIN para pegar nome do solicitante
                "LEFT JOIN AtribuicoesChamado atrib ON c.id_chamado = atrib.id_chamado " +
                "ORDER BY c.dt_atualizacao DESC";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Integer idAgente = (Integer) rs.getObject("id_usuario_atribuido");
                String nota = rs.getString("nota_avaliacao");
                String dataAvaliacao = rs.getString("dt_avaliacao_txt");
                String dataAtualizacao = rs.getString("dt_atualizacao_txt");
                String descricao = rs.getString("desc_chamado");
                String nomeSolicitante = rs.getString("nome_solicitante");

                chamados.add(new ChamadoDTO(
                        rs.getInt("id_chamado"),
                        rs.getString("titulo"),
                        rs.getString("desc_prioridade"),
                        rs.getString("desc_status"),
                        rs.getInt("id_usuario"),
                        idAgente,
                        dataAtualizacao,
                        nota,
                        dataAvaliacao,
                        descricao,
                        nomeSolicitante
                ));
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(chamados));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro SQL ao buscar lista: " + e.getMessage() + "\"}");
        }
    }

    /**
     * MÉTODO AUXILIAR 2: Busca todos os detalhes de UM chamado específico (para a tela Detalhe).
     */
    private void buscarChamadoPorId(HttpServletResponse response, Gson gson, int idChamado) throws IOException {

        List<InteracaoDTO> timeline = buscarTimelineDoChamado(idChamado);

        // ✨ Query corrigida com os nomes das tabelas corretos ✨
        String sql = "SELECT " +
                "  c.id_chamado, c.titulo, c.desc_chamado, CONVERT(varchar, c.dt_abertura, 103) AS dt_abertura, " +
                "  CONVERT(varchar, c.dt_fechamento, 103) AS dt_fechamento, c.email_contato, " +
                "  p.desc_prioridade, " +
                "  s.desc_status, " +
                "  cat.desc_categoria, " +
                "  u_sol.nome_usuario AS nome_solicitante, " +
                "  u_agente.nome_usuario AS nome_agente " +
                "FROM Chamados c " +
                "JOIN Prioridades p ON c.id_prioridade = p.niv_prioridade " +
                "JOIN StatusChamado s ON c.id_status = s.id_status " +
                "JOIN Categorias cat ON c.id_categoria = cat.id_categoria " +
                "JOIN Usuarios u_sol ON c.id_usuario = u_sol.id_usuario " +
                "LEFT JOIN AtribuicoesChamado atrib ON c.id_chamado = atrib.id_chamado " +
                "LEFT JOIN Usuarios u_agente ON atrib.id_usuario_atribuido = u_agente.id_usuario " +
                "WHERE c.id_chamado = ?";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idChamado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ChamadoCompletoDTO chamado = new ChamadoCompletoDTO(
                            rs.getInt("id_chamado"), rs.getString("titulo"), rs.getString("desc_chamado"),
                            rs.getString("dt_abertura"), rs.getString("dt_fechamento"), rs.getString("email_contato"),
                            rs.getString("desc_prioridade"), rs.getString("desc_status"), rs.getString("desc_categoria"),
                            rs.getString("nome_solicitante"), rs.getString("nome_agente"),
                            timeline
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(chamado));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Erro 404
                    response.getWriter().write("{\"success\": false, \"message\": \"Chamado não encontrado.\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro SQL ao buscar detalhes: " + e.getMessage() + "\"}");
        }
    }

    /**
     * MÉTODO AUXILIAR 3: Busca a timeline de interações para um chamado.
     */
    private List<InteracaoDTO> buscarTimelineDoChamado(int idChamado) throws IOException {
        List<InteracaoDTO> timeline = new ArrayList<>();

        String sql = "SELECT " +
                "  i.id_interacoes, i.id_chamado, i.id_usuario, i.mensagem, CONVERT(varchar, i.dt_interacao, 108) AS dt_interacao_formatada, " +
                "  u.nome_usuario " +
                "FROM InteracoesChamado i " +
                "JOIN Usuarios u ON i.id_usuario = u.id_usuario " +
                "WHERE i.id_chamado = ? ORDER BY i.dt_interacao ASC";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idChamado);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    timeline.add(new InteracaoDTO(
                            rs.getInt("id_interacoes"),
                            rs.getInt("id_chamado"),
                            rs.getInt("id_usuario"),
                            rs.getString("nome_usuario"),
                            rs.getString("mensagem"),
                            rs.getString("dt_interacao_formatada")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar timeline: " + e.getMessage());
            e.printStackTrace();
        }
        return timeline;
    }
}