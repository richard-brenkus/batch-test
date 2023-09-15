package org.example.config;

import lombok.AllArgsConstructor;
import org.example.entity.Customer;
import org.example.listener.StepSkipListener;
import org.example.partition.ColumnRangePartitioner;
import org.example.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

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

    public FlatFileItemReader<Customer> reader(){
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
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
    public PartitionHandler partitionHandler(){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(4);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
        taskExecutorPartitionHandler.setStep(workerStep());
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step workerStep() {
        return new StepBuilder("worker-step", jobRepository)
                .<Customer, Customer>chunk(250, transactionManager)
                .reader(reader())
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
    public Step managerStep() {
        return new StepBuilder("manager-step", jobRepository)
                .partitioner(workerStep().getName(), partitioner())
                .partitionHandler(partitionHandler())
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
    public Job runJob(){
        return new JobBuilder("importCustomers", jobRepository)
                .flow(managerStep())
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

        /*SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;*/

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor;
    }


}
