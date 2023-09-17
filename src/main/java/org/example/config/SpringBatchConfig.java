package org.example.config;

import lombok.AllArgsConstructor;
import org.example.entity.Customer;
import org.example.listener.StepSkipListener;
import org.example.partition.ColumnRangePartitioner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
//@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

   // private JobBuilderFactory;
    //private StepBuilderFactory

 //   private CustomerRepository customerRepository;

    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;

    //partitioning:
    private CustomerWriter customerWriter;

//    private String pathFile = "not yet set";

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile){
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(new File(pathToFile)));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    public FlatFileItemReader<Customer> reader(){

        String TEMP_STORAGE = "C:/Data/Batch-files/TEMP_FILE.csv";

        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(TEMP_STORAGE));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Customer> lineMapper(){
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

  //  @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

   /* public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;

    }*/

    @Bean
    public ColumnRangePartitioner partitioner(){
        return new ColumnRangePartitioner();
    }

    @Bean
    public PartitionHandler partitionHandler(FlatFileItemReader<Customer> itemReader){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(4);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
        taskExecutorPartitionHandler.setStep(workerStep(itemReader));
        return taskExecutorPartitionHandler;
    }



    @Bean
    public Step workerStep(FlatFileItemReader<Customer> itemReader) {
        return new StepBuilder("worker-step", jobRepository)
                .<Customer, Customer>chunk(250, transactionManager)
                .reader(itemReader)
                .processor(processor())
                .writer(customerWriter)
                .faultTolerant()
        //        .skipLimit(100)
        //        .skip(Exception.class)
        //        .taskExecutor(taskExecutor())
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .build();
    }

    @Bean
    public Step managerStep(FlatFileItemReader<Customer> itemReader) {
        return new StepBuilder("manager-step", jobRepository)
                .partitioner(workerStep(reader()).getName(), partitioner())
                .partitionHandler(partitionHandler(reader()))
                .build();
    }

   /* @Bean
    public Step step1(){
        return new StepBuilder("csv-step", jobRepository)
                .<Customer, Customer> chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();

    }*/

    @Bean
    public Job runJob(FlatFileItemReader<Customer> itemReader){
        return new JobBuilder("importCustomers", jobRepository)
                .flow(managerStep(itemReader))
                .end()
                .build();
    }

    @Bean
    public SkipPolicy skipPolicy(){
        return new ExceptionSkipPolicy();
    };

    @Bean
    public SkipListener skipListener(){
        return  new StepSkipListener();
    }

    @Bean
    public TaskExecutor taskExecutor(){

       /* SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;*/

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor;
    }


}
