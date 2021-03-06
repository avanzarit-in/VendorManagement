package com.avanzarit.apps.gst.batch.job.vendorimport;

import com.avanzarit.apps.gst.batch.job.ReaderStepListener;
import com.avanzarit.apps.gst.model.Vendor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Created by SPADHI on 5/30/2017.
 */
@Component
public class VendorImportReaderStepListener extends ReaderStepListener<Vendor> {
    private static final Logger LOGGER = LogManager.getLogger(VendorImportReaderStepListener.class);

    @OnReadError
    public void onReadError(Exception exception) {
        super.onReadError(exception);
    }

    @AfterRead
    public void afterRead(Vendor item) {
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
