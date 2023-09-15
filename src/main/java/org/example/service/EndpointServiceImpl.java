package org.example.service;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EndpointServiceImpl implements  EndpointService, SoapService{

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private String TEMP_STORAGE = "C:/Data/Batch-files";

    @Override
    public void importCsvRest(MultipartFile multipartFile) {

        receiveFile(multipartFile);

        /*try {
            String originalFileName = multipartFile.getOriginalFilename();
            File fileToImport = new File(TEMP_STORAGE + originalFileName);
            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", TEMP_STORAGE + originalFileName)
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getExitStatus().getExitCode().equals((ExitStatus.COMPLETED)))
                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void importCsvSoap(MultipartFile multipartFile) {

        receiveFile(multipartFile);

    }

     private void receiveFile(MultipartFile multipartFile){

        try {
            String originalFileName = multipartFile.getOriginalFilename();
            File fileToImport = new File(TEMP_STORAGE + originalFileName);
            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", TEMP_STORAGE + originalFileName)
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getExitStatus().getExitCode().equals((ExitStatus.COMPLETED)))
                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            e.printStackTrace();
        }

    }
}
