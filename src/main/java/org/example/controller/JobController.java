package org.example.controller;

import org.example.entity.Customer;
import org.example.service.EndpointService;
import org.example.service.EndpointServiceImpl;
import org.slf4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobController<T> {

    @Autowired
    private EndpointService endpointService;

    private final String TEMP_STORAGE = "C:\\Data\\Batch-files";

    @PostMapping("/importCustomers")
    public void importCsvToDBJob(@RequestParam("file") MultipartFile multipartFile) {

        endpointService.importCsvRest(multipartFile);
    }
}
