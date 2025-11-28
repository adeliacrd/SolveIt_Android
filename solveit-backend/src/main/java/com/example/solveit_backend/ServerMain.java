package com.example.solveit_backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jakarta.servlet.MultipartConfigElement; // Import necessário

public class ServerMain {

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {

        // Carregar o Driver JDBC
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERRO: Driver JDBC do SQL Server não encontrado.");
            throw e;
        }

        Server server = new Server(SERVER_PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // --- REGISTRO DOS SERVLETS ---

        context.addServlet(new ServletHolder(LoginServlet.class), "/api/login");
        context.addServlet(new ServletHolder(RegisterServlet.class), "/api/register");
        context.addServlet(new ServletHolder(CategoriaServlet.class), "/api/categorias");
        context.addServlet(new ServletHolder(AbrirChamadoServlet.class), "/api/chamados");

        // Novos Servlets
        context.addServlet(new ServletHolder(AtribuicaoServlet.class), "/api/atribuir");
        context.addServlet(new ServletHolder(AtualizarStatusServlet.class), "/api/status");
        context.addServlet(new ServletHolder(ComentarioServlet.class), "/api/comentarios");

        // ✨ UPLOAD (APENAS ESTE BLOCO, NÃO DUPLIQUE!) ✨
        ServletHolder uploadHolder = new ServletHolder(new UploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement(
                        System.getProperty("java.io.tmpdir"),
                        1024 * 1024 * 10, // 10 MB
                        1024 * 1024 * 50, // 50 MB
                        1024 * 1024 * 2   // 2 MB
                )
        );
        context.addServlet(uploadHolder, "/api/upload");
        // (Certifique-se de que não existe outra linha context.addServlet para "/api/upload" acima ou abaixo disso)

        // -----------------------------

        server.setHandler(context);

        System.out.println("-----------------------------------------------------------------");
        System.out.println("API SolveIT (Servidor Jetty) INICIADA na porta " + SERVER_PORT);
        System.out.println("-----------------------------------------------------------------");

        server.start();
        server.join();
    }
}