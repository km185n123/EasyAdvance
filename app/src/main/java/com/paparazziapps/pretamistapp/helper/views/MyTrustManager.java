package com.paparazziapps.pretamistapp.helper.views;


import javax.net.ssl.X509TrustManager;
        import java.security.cert.CertificateException;
        import java.security.cert.X509Certificate;

public class MyTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // No implementado para clientes, puedes dejarlo vacío o lanzar una excepción si es necesario.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // Aquí decides si confiar o no en el servidor. Puedes implementar lógica de validación de certificados aquí.
        // Por ejemplo, puedes verificar la cadena de certificados, fechas de expiración, etc.
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0]; // Devuelve un arreglo vacío o null si no quieres manejar certificados de autoridades.
    }
}
