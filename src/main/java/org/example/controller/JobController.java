package org.example.controller;

import org.example.service.EndpointServiceImpl;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/jobs")
public class JobController {

    /*@Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;*/

    @Autowired
    EndpointServiceImpl endpointServiceImpl;

    private final String TEMP_STORAGE = "C:\\Data\\Batch-files";

    @PostMapping("/importCustomers")
    public void importCsvToDBJob(@RequestParam("file") MultipartFile multipartFile) {

        endpointServiceImpl.importCsvRest(multipartFile);

    }


}
