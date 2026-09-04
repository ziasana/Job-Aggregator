package com.jobaggregator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IngestionSchedulerTest {

    @Mock
    private IngestionService ingestionService;

    @Test
    void runScheduledIngestion_delegatesToIngestionService() {
        IngestionScheduler scheduler = new IngestionScheduler(ingestionService);

        scheduler.runScheduledIngestion();

        verify(ingestionService, times(1)).runIngestion();
    }
}
