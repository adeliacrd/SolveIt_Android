package com.example.solveit_backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import java.sql.DriverManager; // MANTENHA, mas o código não usa diretamente o import aqui
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

        // Mapeia o Servlet de Login para a rota /api/login
        context.addServlet(new ServletHolder(LoginServlet.class), "/api/login");

        context.addServlet(new ServletHolder(RegisterServlet.class), "/api/register");

        server.setHandler(context);

        System.out.println("-----------------------------------------------------------------");
        System.out.println("API SolveIT (Servidor Jetty) INICIADA com sucesso!");
        System.out.println("Acesse: http://localhost:" + SERVER_PORT + "/api/login");
        System.out.println("-----------------------------------------------------------------");

        server.start();
        server.join();

        /*while (server.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignorar
            }
        }*/
    }
}