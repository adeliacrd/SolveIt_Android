package com.example.solveit.api; // USE O SEU PACOTE CORRETO (ex: com.solveit.api)

// Esta classe mapeia o JSON que sua API (RegisterServlet) retorna
public class RegisterResponse {
    // Os nomes das variáveis devem ser EXATAMENTE iguais às chaves do JSON:
    // {"success": true, "message": "..."}
    private boolean success;
    private String message;

    // Getters são essenciais para o Retrofit ler os dados
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
