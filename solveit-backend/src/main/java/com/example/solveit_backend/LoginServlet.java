package com.example.solveit_backend;

import com.google.gson.Gson;
import java.io.IOException;
// Não precisamos mais das importações de banco de dados, então elas podem ser removidas ou comentadas
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    // As credenciais antigas não são mais usadas, mas podem permanecer aqui sem problemas.
    // #######################################################################
    // DADOS DE CONEXÃO COM O SQL SERVER (TEMPORARIAMENTE DESATIVADOS)
    // #######################################################################
    private static final String JDBC_URL = "jdbc:sqlserver://DESKTOP-PR12T5D\\SQLEXPRESS01:1433;databaseName=SOC_SOLVE;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "gabadaro";
    private static final String PASSWORD = "Gabi0204";
    // #######################################################################

    private static class LoginResponse {
        boolean success;
        String message;
        Integer id_acesso;

        public LoginResponse(boolean success, String message, Integer id_acesso) {
            this.success = success;
            this.message = message;
            this.id_acesso = id_acesso;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // O código original que valida e conecta ao banco de dados foi substituído por este bloco.

        // #####################################################################
        // ### MODO DE DESENVOLVIMENTO: BYPASS DE LOGIN ATIVADO              ###
        // ### Responde que o login foi bem-sucedido para qualquer usuário.    ###
        // #####################################################################

        System.out.println("============================================================");
        System.out.println("AVISO: O MODO DE BYPASS DE LOGIN ESTÁ ATIVO NO SERVIDOR!");
        System.out.println("Qualquer tentativa de login será aprovada automaticamente.");
        System.out.println("============================================================");

        // 1. Cria uma resposta de sucesso Falsa, como se o login tivesse funcionado.
        // O "id_acesso: 1" geralmente significa que é um usuário administrador.
        LoginResponse respostaFalsa = new LoginResponse(true, "Login de desenvolvedor bem-sucedido!", 1);

        Gson gson = new Gson();

        // 2. Envia a resposta de sucesso para o aplicativo.
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(respostaFalsa));

        // E é só isso. O servidor não tentará mais se conectar ao banco de dados.
    }
}
