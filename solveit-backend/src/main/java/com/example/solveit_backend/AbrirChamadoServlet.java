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
    // ✨ 1. DTOs INTERNOS (MOLDES DE DADOS) ✨
    // ==========================================================

    // DTO para a Timeline (Chat)
    private static class InteracaoDTO {
        private int id_interacoes; private int id_chamado; private int id_usuario;
        private String nome_usuario; private String mensagem; private String dt_interacao;

        public InteracaoDTO(int id_interacoes, int id_chamado, int id_usuario, String nome_usuario, String mensagem, String dt_interacao) {
            this.id_interacoes = id_interacoes; this.id_chamado = id_chamado; this.id_usuario = id_usuario;
            this.nome_usuario = nome_usuario; this.mensagem = mensagem; this.dt_interacao = dt_interacao;
        }
    }

    // DTO para a Lista Resumida (Home)
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

    // DTO para o Detalhe Completo (Informações)
    private static class ChamadoCompletoDTO {
        int id_chamado; String titulo; String desc_chamado; String dt_abertura; String dt_fechamento; String email_contato;
        String desc_prioridade; String desc_status; String desc_categoria; String nome_solicitante; String nome_agente;
        String email_solicitante; String email_agente;
        int sla_horas;
        int id_usuario; // ID do Criador
        Integer id_usuario_atribuido; // ID do Agente (pode ser nulo)
        List<InteracaoDTO> timeline;

        // Construtor com 17 argumentos (Sincronizado com a query)
        public ChamadoCompletoDTO(int id, String t, String d, String da, String df, String e,
                                  String dp, String ds, String dc, String ns, String na,
                                  String es, String ea, int sla, int uid, Integer id_atrib,
                                  List<InteracaoDTO> tl) {
            id_chamado = id; titulo = t; desc_chamado = d; dt_abertura = da; dt_fechamento = df; email_contato = e;
            desc_prioridade = dp; desc_status = ds; desc_categoria = dc; nome_solicitante = ns; nome_agente = na;
            email_solicitante = es; email_agente = ea; sla_horas = sla; id_usuario = uid; id_usuario_atribuido = id_atrib;
            timeline = tl;
        }
    }

    // Resposta Padrão
    private static class AbrirChamadoResponse {
        boolean success; String message; Integer id_chamado;
        public AbrirChamadoResponse(boolean s, String m, Integer i) { success = s; message = m; id_chamado = i; }
        public AbrirChamadoResponse(boolean s, String m) { this(s, m, null); }
    }

    // ==========================================================
    // ✨ 2. MÉTODO doPost (CRIAR CHAMADO) ✨
    // ==========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json; charset=UTF-8");

        String titulo = request.getParameter("titulo");
        String idUsuarioAberturaStr = request.getParameter("id_usuario_abertura");
        String prioridadeTexto = request.getParameter("prioridade");
        String idCategoriaStr = request.getParameter("id_categoria");
        String email = request.getParameter("email");
        String descricao = request.getParameter("descricao");

        if (titulo == null || titulo.trim().isEmpty() || idUsuarioAberturaStr == null || prioridadeTexto == null || idCategoriaStr == null || email == null || descricao == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Campos obrigatórios faltando.")));
            return;
        }

        int idCategoria = -1, idUsuarioAbertura = -1, idPrioridade = -1;
        switch (prioridadeTexto) {
            case "Urgente": idPrioridade = 1; break;
            case "Alta": idPrioridade = 2; break;
            case "Média": idPrioridade = 3; break;
            case "Baixa": idPrioridade = 4; break;
        }

        try {
            idCategoria = Integer.parseInt(idCategoriaStr);
            idUsuarioAbertura = Integer.parseInt(idUsuarioAberturaStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "IDs inválidos.")));
            return;
        }

        String sql = "INSERT INTO Chamados (titulo, id_usuario, id_prioridade, email_contato, desc_chamado, id_categoria, dt_abertura, dt_atualizacao, id_status) VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), 1)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, titulo);
            pstmt.setInt(2, idUsuarioAbertura);
            pstmt.setInt(3, idPrioridade);
            pstmt.setString(4, email);
            pstmt.setString(5, descricao);
            pstmt.setInt(6, idCategoria);

            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                int novoId = rs.next() ? rs.getInt(1) : -1;
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(new AbrirChamadoResponse(true, "Chamado criado!", novoId)));
            } else {
                response.setStatus(500);
                response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Erro ao inserir.")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(gson.toJson(new AbrirChamadoResponse(false, "Erro SQL: " + e.getMessage())));
        }
    }

    // ==========================================================
    // ✨ 3. MÉTODO doGet (BUSCAR) ✨
    // ==========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json; charset=UTF-8");
        String idChamadoParam = request.getParameter("id_chamado");

        if (idChamadoParam == null || idChamadoParam.isEmpty()) {
            buscarListaDeChamados(response, gson);
        } else {
            try {
                buscarChamadoPorId(response, gson, Integer.parseInt(idChamadoParam));
            } catch (NumberFormatException e) {
                response.setStatus(400);
            }
        }
    }

    private void buscarListaDeChamados(HttpServletResponse response, Gson gson) throws IOException {
        List<ChamadoDTO> chamados = new ArrayList<>();
        String sql = "SELECT c.id_chamado, c.titulo, c.id_usuario, c.nota_avaliacao, c.desc_chamado, " +
                "CONVERT(VARCHAR(20), c.dt_avaliacao, 103) AS dt_av, " +
                "CONVERT(VARCHAR(20), c.dt_atualizacao, 120) AS dt_up, " +
                "p.desc_prioridade, s.desc_status, atrib.id_usuario_atribuido, u.nome_usuario " +
                "FROM Chamados c " +
                "JOIN Prioridades p ON c.id_prioridade = p.niv_prioridade " +
                "JOIN StatusChamado s ON c.id_status = s.id_status " +
                "JOIN Usuarios u ON c.id_usuario = u.id_usuario " +
                "LEFT JOIN AtribuicoesChamado atrib ON c.id_chamado = atrib.id_chamado " +
                "ORDER BY c.dt_atualizacao DESC";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                chamados.add(new ChamadoDTO(
                        rs.getInt("id_chamado"), rs.getString("titulo"), rs.getString("desc_prioridade"), rs.getString("desc_status"),
                        rs.getInt("id_usuario"), (Integer) rs.getObject("id_usuario_atribuido"),
                        rs.getString("dt_up"), rs.getString("nota_avaliacao"), rs.getString("dt_av"),
                        rs.getString("desc_chamado"), rs.getString("nome_usuario")
                ));
            }
            response.getWriter().write(gson.toJson(chamados));
        } catch (Exception e) { e.printStackTrace(); response.setStatus(500); }
    }

    private void buscarChamadoPorId(HttpServletResponse response, Gson gson, int idChamado) throws IOException {
        List<InteracaoDTO> timeline = buscarTimelineDoChamado(idChamado);

        // Query Completa
        String sql = "SELECT c.id_chamado, c.titulo, c.desc_chamado, c.email_contato, c.id_usuario, " +
                "CONVERT(varchar, c.dt_abertura, 103) + ' ' + CONVERT(varchar, c.dt_abertura, 108) AS dt_ab, " +
                "CONVERT(varchar, c.dt_fechamento, 103) + ' ' + CONVERT(varchar, c.dt_fechamento, 108) AS dt_fc, " +
                "p.desc_prioridade, s.desc_status, cat.desc_categoria, " +
                "u_sol.nome_usuario AS nome_solicitante, u_sol.email AS email_solicitante, " +
                "u_agente.nome_usuario AS nome_agente, u_agente.email AS email_agente, " +
                "sla.temp_solucao_horas AS sla_horas, " +
                "atrib.id_usuario_atribuido " +
                "FROM Chamados c " +
                "JOIN Prioridades p ON c.id_prioridade = p.niv_prioridade " +
                "JOIN StatusChamado s ON c.id_status = s.id_status " +
                "JOIN Categorias cat ON c.id_categoria = cat.id_categoria " +
                "JOIN Usuarios u_sol ON c.id_usuario = u_sol.id_usuario " +
                "LEFT JOIN AtribuicoesChamado atrib ON c.id_chamado = atrib.id_chamado " +
                "LEFT JOIN Usuarios u_agente ON atrib.id_usuario_atribuido = u_agente.id_usuario " +
                "LEFT JOIN SLA sla ON p.niv_prioridade = sla.id_prioridade " +
                "WHERE c.id_chamado = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idChamado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ChamadoCompletoDTO dto = new ChamadoCompletoDTO(
                            rs.getInt("id_chamado"), rs.getString("titulo"), rs.getString("desc_chamado"),
                            rs.getString("dt_ab"), rs.getString("dt_fc"), rs.getString("email_contato"),
                            rs.getString("desc_prioridade"), rs.getString("desc_status"), rs.getString("desc_categoria"),
                            rs.getString("nome_solicitante"), rs.getString("nome_agente"),
                            rs.getString("email_solicitante"), rs.getString("email_agente"),
                            rs.getInt("sla_horas"), rs.getInt("id_usuario"),
                            (Integer) rs.getObject("id_usuario_atribuido"),
                            timeline
                    );
                    response.getWriter().write(gson.toJson(dto));
                } else { response.setStatus(404); }
            }
        } catch (Exception e) { e.printStackTrace(); response.setStatus(500); }
    }

    private List<InteracaoDTO> buscarTimelineDoChamado(int idChamado) {
        List<InteracaoDTO> timeline = new ArrayList<>();
        String sql = "SELECT i.id_interacoes, i.id_chamado, i.id_usuario, i.mensagem, " +
                "CONVERT(varchar, i.dt_interacao, 108) AS dt_fmt, u.nome_usuario " +
                "FROM InteracoesChamado i JOIN Usuarios u ON i.id_usuario = u.id_usuario " +
                "WHERE i.id_chamado = ? ORDER BY i.dt_interacao ASC";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idChamado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    timeline.add(new InteracaoDTO(
                            rs.getInt("id_interacoes"), rs.getInt("id_chamado"), rs.getInt("id_usuario"),
                            rs.getString("nome_usuario"), rs.getString("mensagem"), rs.getString("dt_fmt")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return timeline;
    }
}