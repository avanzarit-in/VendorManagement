package com.avanzarit.apps.gst.batch.job.vendorimport;

import com.avanzarit.apps.gst.model.Vendor;
import com.avanzarit.apps.gst.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.util.List;

public class VendorDataImportListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(VendorDataImportListener.class);

    private final VendorRepository vendorRepository;

    public VendorDataImportListener(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Finish Job! Check the results");

            List<Vendor> vendors = vendorRepository.findAll();

            for (Vendor vendor : vendors) {
                log.info("Found <" + vendor + "> in the database.");
            }
        }
    }
}