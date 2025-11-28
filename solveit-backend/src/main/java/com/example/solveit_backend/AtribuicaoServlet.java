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

public class AtribuicaoServlet extends HttpServlet {

    // Classe de resposta interna
    private static class AtribuicaoResponse {
        boolean success;
        String message;
        public AtribuicaoResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idChamadoStr = request.getParameter("id_chamado");
        String idUsuarioStr = request.getParameter("id_usuario"); // O ID do Agente

        if (idChamadoStr == null || idUsuarioStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new AtribuicaoResponse(false, "IDs obrigatórios.")));
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword())) {

            int idChamado = Integer.parseInt(idChamadoStr);
            int idUsuario = Integer.parseInt(idUsuarioStr);

            // 1. Criar a atribuição
            // (Certifique-se que o nome da tabela é AtribuicoesChamado e a coluna é id_usuario_atribuido, como vimos antes)
            String sqlInsert = "INSERT INTO AtribuicoesChamado (id_chamado, id_usuario_atribuido, dt_atribuicao) VALUES (?, ?, GETDATE())";
            PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, idChamado);
            pstmtInsert.setInt(2, idUsuario);
            pstmtInsert.executeUpdate();

            // 2. Atualizar o status do chamado para "Em Atendimento" (ID 2)
            String sqlUpdate = "UPDATE Chamados SET id_status = 2, dt_atualizacao = GETDATE() WHERE id_chamado = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, idChamado);
            pstmtUpdate.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(new AtribuicaoResponse(true, "Chamado atribuído com sucesso!")));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new AtribuicaoResponse(false, "Erro no servidor: " + e.getMessage())));
        }
    }
}