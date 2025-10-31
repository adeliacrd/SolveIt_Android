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
import java.util.ArrayList;
import java.util.List;

public class CategoriaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<CategoriaDTO> categorias = new ArrayList<>();
        String sql = "SELECT id_categoria, desc_categoria FROM Categorias ORDER BY desc_categoria ASC"; // Ordena alfabeticamente

        // Usa try-with-resources para garantir que a conexão seja fechada
        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getDbUrl(),
                DatabaseConfig.getDbUsername(),
                DatabaseConfig.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Itera pelos resultados do banco
            while (rs.next()) {
                int id = rs.getInt("id_categoria");
                String descricao = rs.getString("desc_categoria");
                categorias.add(new CategoriaDTO(id, descricao)); // Adiciona à lista
            }

            // Converte a lista para JSON e envia como resposta
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            response.getWriter().write(gson.toJson(categorias));

        } catch (SQLException e) {
            e.printStackTrace(); // Loga o erro no console do servidor
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            // Envia uma resposta de erro genérica (poderia ser um JSON também)
            response.getWriter().write("{\"success\": false, \"message\": \"Erro ao buscar categorias do banco de dados.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro inesperado no servidor.\"}");
        }
    }
}
