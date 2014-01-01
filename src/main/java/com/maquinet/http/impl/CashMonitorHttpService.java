package com.maquinet.http.impl;

import com.maquinet.http.HttpService;
import org.apache.http.client.HttpClient;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class CashMonitorHttpService implements HttpService
{
    private final String serviceEndpoint;
    private final HttpClient httpClient;

    public CashMonitorHttpService(HttpClient httpClient, String serviceEndpoint)
    {
        this.httpClient = httpClient;
        this.serviceEndpoint = serviceEndpoint;
    }

    @Override
    public HttpClient getHttpClient()
    {
        return httpClient;
    }

    @Override
    public String getServiceEndpoint()
    {
        return serviceEndpoint;
    }
}
