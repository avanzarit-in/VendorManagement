package com.avanzarit.apps.gst.batch.job.customercontactpersonimport;

import com.avanzarit.apps.gst.batch.job.ReaderStepListener;
import com.avanzarit.apps.gst.model.CustomerContactPersonMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.stereotype.Component;

@Component
public class CustomerContactPersonImportReaderStepListener extends ReaderStepListener<CustomerContactPersonMaster> {

    private static final Logger LOGGER = LogManager.getLogger(CustomerContactPersonImportReaderStepListener.class);

    @OnReadError
    public void onReadError(Exception exception) {
        super.onReadError(exception);
    }

    @AfterRead
    public void afterRead(CustomerContactPersonMaster item) {
        super.afterReadItem(item);
    }


    @BeforeRead
    public void beforeRead() {
        super.beforeRead();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        super.beforeStep(stepExecution);
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        super.afterStep();
        return ExitStatus.COMPLETED;
    }


}
