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
import java.sql.SQLException;

public class AtualizarStatusServlet extends HttpServlet {

    private static class StatusResponse {
        boolean success; String message;
        public StatusResponse(boolean s, String m) { success = s; message = m; }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idChamadoStr = request.getParameter("id_chamado");
        String novoStatusStr = request.getParameter("novo_status"); // 4 para Concluir, 5 para Cancelar

        if (idChamadoStr == null || novoStatusStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new StatusResponse(false, "IDs obrigatórios.")));
            return;
        }

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword())) {

            int idChamado = Integer.parseInt(idChamadoStr);
            int novoStatus = Integer.parseInt(novoStatusStr);

            // SQL: Atualiza o status, a data de fechamento (se for final) e a data de atualização
            // Se o status for 4 (Concluído) ou 5 (Cancelado), preenche dt_fechamento com GETDATE()
            // Se for outro status, dt_fechamento fica como está (ou NULL)
            String sql = "UPDATE Chamados SET " +
                    "id_status = ?, " +
                    "dt_atualizacao = GETDATE(), " +
                    "dt_fechamento = CASE WHEN ? IN (4, 5) THEN GETDATE() ELSE dt_fechamento END " +
                    "WHERE id_chamado = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, novoStatus);
            pstmt.setInt(2, novoStatus); // Para o CASE verificar se é 4 ou 5
            pstmt.setInt(3, idChamado);

            int linhas = pstmt.executeUpdate();

            if (linhas > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(new StatusResponse(true, "Status atualizado com sucesso!")));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new StatusResponse(false, "Erro ao atualizar.")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new StatusResponse(false, "Erro: " + e.getMessage())));
        }
    }
}
