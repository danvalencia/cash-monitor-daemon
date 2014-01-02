package com.maquinet.services;

import org.apache.http.client.HttpClient;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface HttpService
{
    HttpClient getHttpClient();
    String getServiceEndpoint();
}
