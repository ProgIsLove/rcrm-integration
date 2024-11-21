package com.client.rcrm.integration.raynet.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImportJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job {} started", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        this.writeCount(jobExecution);
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job {} completed", jobExecution.getJobInstance().getJobName());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("Job {} failed", jobExecution.getJobInstance().getJobName());
        }
    }

    private void writeCount(JobExecution jobExecution) {
        jobExecution.getStepExecutions().stream().findFirst().ifPresent(stepExecution -> {
            long writeCount = stepExecution.getWriteCount();
            long skipCount = stepExecution.getSkipCount();
            log.info("the job has written {} lines", writeCount);
            log.info("the job has skip {} lines", skipCount);
        });
    }
}
