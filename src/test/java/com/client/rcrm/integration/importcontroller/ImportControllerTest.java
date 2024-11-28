package com.client.rcrm.integration.importcontroller;

import com.client.rcrm.integration.raynet.batch.controller.ImportController;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.RaynetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ImportControllerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private ValidationService validationService;

    @Mock
    private RaynetService raynetService;

    @InjectMocks
    private ImportController importController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(importController).build();
    }

    @Test
    void importCsvToDBJob_shouldReturnOk_whenFileIsValidAndJobCompletes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "id,name\n1,John".getBytes()
        );

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        mockMvc.perform(multipart("/host/uploadData")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("COMPLETED"));

        verify(validationService).isFileValid(file, ".csv");
        verify(jobLauncher).run(any(Job.class), any(JobParameters.class));
        verify(raynetService).syncCompaniesWithRaynetPaginated(10000);
    }
}
