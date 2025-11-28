package com.example.solveit_backend;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

// Configuração para aceitar arquivos
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class UploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/SolveIT_Arquivos"; // Certifique-se que essa pasta existe!

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        try {
            // Recebe os campos
            String idChamadoStr = req.getParameter("id_chamado");
            Part filePart = req.getPart("arquivo");

            if (idChamadoStr == null || filePart == null) {
                resp.getWriter().write("{\"success\":false, \"message\":\"Faltam dados (id ou arquivo).\"}");
                return;
            }

            // 1. Salvar arquivo no Disco
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdir();

            String fileName = getFileName(filePart);
            String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
            String fullPath = UPLOAD_DIR + File.separator + uniqueFileName;

            filePart.write(fullPath);

            // 2. Salvar no Banco (QUERY CORRIGIDA)
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.getDbUrl(), DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword())) {

                // ✨ CORREÇÃO AQUI: Mudado de 'id_nome_arquivo' para 'nome_arquivo' ✨
                String sql = "INSERT INTO AnexosChamado (id_chamado, nome_arquivo, caminho_arquivo, dt_upload) VALUES (?, ?, ?, GETDATE())";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(idChamadoStr));
                pstmt.setString(2, fileName); // Nome original do arquivo
                pstmt.setString(3, fullPath); // Caminho completo no servidor
                pstmt.executeUpdate();
            }

            resp.getWriter().write("{\"success\":true, \"message\":\"Arquivo enviado com sucesso!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"success\":false, \"message\":\"Erro no upload: " + e.getMessage() + "\"}");
        }
    }

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return "desconhecido.file";
    }
}
