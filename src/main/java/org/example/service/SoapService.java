package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceProvider;

@WebServiceProvider
public interface SoapService {

    @WebEndpoint
    void importCsvSoap(MultipartFile multipartFile);
}
