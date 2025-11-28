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

public class ComentarioServlet extends HttpServlet {

    private static class ResponseData { boolean success; String message; public ResponseData(boolean s, String m) { success = s; message = m; } }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json; charset=UTF-8");

        String idChamado = req.getParameter("id_chamado");
        String idUsuario = req.getParameter("id_usuario");
        String mensagem = req.getParameter("mensagem");

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword())) {
            // 1. Inserir Interação
            String sql = "INSERT INTO InteracoesChamado (id_chamado, id_usuario, mensagem, dt_interacao) VALUES (?, ?, ?, GETDATE())";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(idChamado));
            pstmt.setInt(2, Integer.parseInt(idUsuario));
            pstmt.setString(3, mensagem);
            pstmt.executeUpdate();

            // 2. Atualizar data de atualização do chamado (para ele subir na lista)
            String sqlUpdate = "UPDATE Chamados SET dt_atualizacao = GETDATE() WHERE id_chamado = ?";
            PreparedStatement pstmtUp = conn.prepareStatement(sqlUpdate);
            pstmtUp.setInt(1, Integer.parseInt(idChamado));
            pstmtUp.executeUpdate();

            resp.getWriter().write(gson.toJson(new ResponseData(true, "Mensagem enviada.")));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(new ResponseData(false, "Erro: " + e.getMessage())));
        }
    }
}