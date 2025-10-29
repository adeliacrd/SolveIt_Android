package com.example.solveit_backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder; // Certifique-se que este import está presente

// Mantenha os outros imports como estavam
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;


public class ServerMain {

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {

        // Carregar o Driver JDBC do SQL Server (CRUCIAL)
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERRO: Driver JDBC do SQL Server não encontrado.");
            throw e; // Interrompe se o driver não estiver na dependência
        }

        Server server = new Server(SERVER_PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Mapeia os Servlets existentes
        context.addServlet(new ServletHolder(LoginServlet.class), "/api/login");
        context.addServlet(new ServletHolder(RegisterServlet.class), "/api/register");
        context.addServlet(new ServletHolder(CategoriaServlet.class), "/api/categorias");
        context.addServlet(new ServletHolder(AbrirChamadoServlet.class), "/api/chamados");

        server.setHandler(context);

        System.out.println("-----------------------------------------------------------------");
        System.out.println("API SolveIT (Servidor Jetty) INICIADA com sucesso!");
        System.out.println("Endpoints disponíveis:"); // Mensagem atualizada para clareza
        System.out.println("  POST /api/login");
        System.out.println("  POST /api/register");
        System.out.println("  GET  /api/categorias");
        System.out.println("  POST /api/chamados"); // Novo endpoint
        System.out.println("-----------------------------------------------------------------");

        server.start();
        server.join();
    }
}