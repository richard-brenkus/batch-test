package org.example.service;

import org.example.entity.Customer;
import org.example.repository.CustomerRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class EndpointServiceImpl implements EndpointService{

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    CustomerRepository customerRepository;

    private String TEMP_STORAGE = "C:/Data/Batch-files/";

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
    public ResponseEntity<Optional<Customer>> getCustomerDetails(int customerId) {
       Optional<Customer> customer = customerRepository.findById(customerId);
       return new ResponseEntity<Optional<Customer>>(customer, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Optional<List<Customer>>> getAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        Optional<List<Customer>> allCustomers = Optional.ofNullable(customerList);
        return new ResponseEntity<Optional<List<Customer>>>(allCustomers, HttpStatus.OK);
    }

    public Path receiveFile(MultipartFile multipartFile){
        Path filePath = Path.of(TEMP_STORAGE + "TEMP_FILE.csv");
        System.out.println("TEMP_FILE.csv filePath: " + filePath);

        try {
            String originalFileName = multipartFile.getOriginalFilename();
            File fileToImport = new File(TEMP_STORAGE + originalFileName);
            multipartFile.transferTo(fileToImport);
            new File(filePath.toString());
            Files.copy(Path.of(TEMP_STORAGE + originalFileName), filePath, StandardCopyOption.REPLACE_EXISTING);
            if(Files.exists(filePath))
                System.out.println("temp file on path " + filePath + " exists");
            else
                System.out.println("temp file does not exist");

        } catch (/*JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException |*/ IOException e) {
            e.printStackTrace();
        }
        return filePath;

    }

    public void processFile(Path filePath){

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", filePath.toString())
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getExitStatus().equals((ExitStatus.COMPLETED))) {
                System.out.println("Job exit status is COMPLETED, deleting files: ");
                System.out.println(filePath.toString());
                System.out.println(TEMP_STORAGE + "TEMP_FILE.csv");
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(Path.of(TEMP_STORAGE + "TEMP_FILE.csv"));
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            e.printStackTrace();
        }

    }
}
