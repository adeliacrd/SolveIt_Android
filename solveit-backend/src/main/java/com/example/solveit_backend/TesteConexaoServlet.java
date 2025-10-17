package com.example.solveit_backend;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TesteConexaoServlet extends HttpServlet {

    // #######################################################################
    // DADOS DE CONEXÃO COM O SQL SERVER (!!! AJUSTE ISTO !!!)
    // #######################################################################
    private static final String JDBC_URL = "jdbc:sqlserver://DESKTOP-PR12T5D\\SQLEXPRESS01:1433;databaseName=SOC_SOLVE;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "gabadaro";
    private static final String PASSWORD = "Gabi0204";
    // #######################################################################

    private static class StatusResponse {
        String status;
        String database;

        public StatusResponse(String status, String database) {
            this.status = status;
            this.database = database;
        }
    }

    // Usamos o método doGet() para que o navegador possa testar (http://localhost:8080/api/status)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String statusConexao = "FALHA";
        String databaseNome = "Nenhum";

        // 1. Tentar Conectar ao SQL Server
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {

            // Se a linha acima passar, a conexão funcionou!
            statusConexao = "SUCESSO!";
            databaseNome = conn.getCatalog();

        } catch (SQLException e) {
            // Se falhar, captura o erro exato do SQL Server.
            statusConexao = "ERRO: " + e.getMessage();
            e.printStackTrace();

        } catch (Exception e) {
            statusConexao = "ERRO: Driver ou URL incorreta.";
            e.printStackTrace();
        }

        // 2. Retornar Status
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(new StatusResponse(statusConexao, databaseNome)));
    }
}