package org.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface EndpointService {

    void importCsvRest(MultipartFile multipartFile);

}
