package com.avanzarit.apps.gst.batch.job.controller;

import com.avanzarit.apps.gst.auth.repository.RoleRepository;
import com.avanzarit.apps.gst.auth.repository.UserRepository;
import com.avanzarit.apps.gst.auth.service.UserService;
import com.avanzarit.apps.gst.batch.job.customerimport.CustomerDataImportListener;
import com.avanzarit.apps.gst.batch.job.customerimport.CustomerDataImportProcessor;
import com.avanzarit.apps.gst.batch.job.customerimport.CustomerDataImportReader;
import com.avanzarit.apps.gst.batch.job.customerimport.CustomerDataImportWriter;
import com.avanzarit.apps.gst.batch.job.vendorexport.VendorDataExportProcessor;
import com.avanzarit.apps.gst.batch.job.vendorexport.VendorDataExportReader;
import com.avanzarit.apps.gst.batch.job.vendorexport.VendorDataExportWriter;
import com.avanzarit.apps.gst.batch.job.vendorimport.VendorDataImportListener;
import com.avanzarit.apps.gst.batch.job.vendorimport.VendorDataImportProcessor;
import com.avanzarit.apps.gst.batch.job.vendorimport.VendorDataImportReader;
import com.avanzarit.apps.gst.batch.job.vendorimport.VendorDataImportWriter;
import com.avanzarit.apps.gst.email.EmailServiceImpl;
import com.avanzarit.apps.gst.model.Customer;
import com.avanzarit.apps.gst.model.Vendor;
import com.avanzarit.apps.gst.repository.CustomerRepository;
import com.avanzarit.apps.gst.repository.VendorRepository;
import com.avanzarit.apps.gst.storage.StorageFileNotFoundException;
import com.avanzarit.apps.gst.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.DataSource;

/**
 * Created by SPADHI on 5/5/2017.
 */
@Controller
public class BatchJobController implements BeanFactoryAware {

    private BeanFactory beanFactory;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    public SimpleMailMessage updatePasswordMessage;
    @Autowired
    public EmailServiceImpl emailService;
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    StorageService storageService;
    @Autowired
    public VendorRepository vendorRepository;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Qualifier("dataSource")
    @Autowired
    public DataSource dataSource;
    @Autowired
    private UserService userService;


    public Job exportVendorJob() {
        return jobBuilderFactory.get("exportVendorjob").incrementer(new RunIdIncrementer())
                .flow(exportVendorStep()).end().build();
    }


    public Step exportVendorStep() {
        return stepBuilderFactory.get("exportVendorStep").<Vendor, Vendor>chunk(2)
                .reader(VendorDataExportReader.reader(dataSource))
                .processor(new VendorDataExportProcessor()).writer(VendorDataExportWriter.write(storageService)).build();
    }

    public Job importVendorJob() {
        return jobBuilderFactory.get("importVendorjob").incrementer(new RunIdIncrementer()).listener(new VendorDataImportListener(vendorRepository))
                .flow(importVendorStep()).end().build();
    }


    public Step importVendorStep() {
        return stepBuilderFactory.get("importVendorStep").<Vendor, Vendor>chunk(2)
                .reader(VendorDataImportReader.reader(storageService))
                .processor(new VendorDataImportProcessor()).writer(new VendorDataImportWriter(vendorRepository, userService, updatePasswordMessage, emailService)).build();
    }


    public Job importCustomerJob() {
        return jobBuilderFactory.get("importCustomerjob").incrementer(new RunIdIncrementer()).listener(new CustomerDataImportListener(customerRepository))
                .flow(importCustomerStep()).end().build();
    }


    public Step importCustomerStep() {
        return stepBuilderFactory.get("importCustomerStep").<Customer, Customer>chunk(2)
                .reader(CustomerDataImportReader.reader(storageService))
                .processor(new CustomerDataImportProcessor())
                .writer(new CustomerDataImportWriter(customerRepository, userService, updatePasswordMessage, emailService)).build();
    }

    @GetMapping("/upload")
    public String getVendorDataUploadForm() {

        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            storageService.store(file);
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(importVendorJob(), jobParameters);
            //   storageService.deleteAll();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload";
    }


    @PostMapping("/userupload")
    public String handleUserFileUpload(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            storageService.store(file);
            Job job=(Job)beanFactory.getBean("userImportJob",storageService.loadAsResource(file.getOriginalFilename()));
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload";
    }

    @PostMapping("/customerupload")
    public String handleCustomerFileUpload(@RequestParam("file") MultipartFile file,
                                           RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            storageService.store(file);
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(importCustomerJob(), jobParameters);
            //   storageService.deleteAll();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload";
    }

    @GetMapping("/download")
    public String handleFileDownload(RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {

            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(exportVendorJob(), jobParameters);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return "redirect:/vendorListView";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
}