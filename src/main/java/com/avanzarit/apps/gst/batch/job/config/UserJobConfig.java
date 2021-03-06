package com.avanzarit.apps.gst.batch.job.config;

import com.avanzarit.apps.gst.auth.model.User;
import com.avanzarit.apps.gst.batch.job.policy.SkipPolicy;
import com.avanzarit.apps.gst.batch.job.properties.BatchProperties;
import com.avanzarit.apps.gst.batch.job.userimport.*;
import com.avanzarit.apps.gst.storage.StorageService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;

@Configuration
public class UserJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    BatchProperties batchProperties;

    @Autowired
    StorageService storageService;

    @Autowired
    UserFieldSetMapper userFieldSetMapper;

    @Autowired
    UserDataProcessor userDataProcessor;

    @Autowired
    UserDataWriter userDataWriter;

    @Autowired
    UserJobListener userJobListener;

    @Autowired
    UserReaderStepListener userReaderStepListener;

    @Autowired
    UserWriterStepListener userWriterStepListener;


    public ItemReader<User> reader(Resource resource){
        FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
        reader.setLinesToSkip(1);
        reader.setResource(resource);
        reader.setLineMapper(userLineMapper());
        return reader;
    }

    @Bean
    public LineMapper<User> userLineMapper() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<User>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"USERNAME", "EMAIL", "ROLES"});
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<User>();
        fieldSetMapper.setTargetType(User.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(userFieldSetMapper);
        return lineMapper;
    }

    @Bean(name = "userImportJob")
    @Scope(scopeName = "prototype")
    public Job importUserJob(Resource resource) {
        return jobBuilderFactory.get("importUserjob").incrementer(new RunIdIncrementer()).listener(userJobListener)
                .flow(importUserStep(resource)).end().build();
    }


    public Step importUserStep(Resource resource) {
        return stepBuilderFactory.get("importUserStep").<User, User>chunk(1)
             .faultTolerant().skipPolicy(new SkipPolicy())
                .reader(reader(resource)).listener(userReaderStepListener)
                .processor(userDataProcessor)
                .writer(userDataWriter).listener(userWriterStepListener).build();
    }
}
