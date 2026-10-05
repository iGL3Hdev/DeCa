package com.decamanager.deca;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties (prefix = "deca")
public class DecaProperties {

    private String baseUrl;
    private String directorioDocumentos;

    public String getBaseUrl() {return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getDirectorioDocumentos() { return directorioDocumentos; }
    public void setDirectorioDocumentos(String directorioDocumentos) {
        this.directorioDocumentos = directorioDocumentos;
    }
    
}
