package com.avanzarit.apps.gst.batch.job.contactpersonimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class ContactPersonImportJobListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(ContactPersonImportJobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Finish Job! Check the results");
        }
    }
}
