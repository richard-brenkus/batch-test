package org.example.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface EndpointService<T> {

    void importCsvRest(MultipartFile multipartFile);

    T getCustomerDetails(int customerId);

    T getAllCustomers();

}
