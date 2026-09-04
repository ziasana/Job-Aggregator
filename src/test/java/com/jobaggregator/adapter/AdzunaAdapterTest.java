package com.jobaggregator.adapter;

import com.jobaggregator.config.AdzunaProperties;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AdzunaAdapterTest {

    private static final String BASE_URL = "https://api.adzuna.com/v1/api/jobs";

    @Test
    void fetchJobs_mapsFieldsAndStopsWhenNoCredentials() {
        RestClient.Builder builder = RestClient.builder();
        AdzunaProperties noCreds = new AdzunaProperties(BASE_URL, "de", "", "", 5);
        AdzunaAdapter adapter = new AdzunaAdapter(builder, noCreds);

        assertThat(adapter.fetchJobs()).isEmpty();
    }

    @Test
    void fetchJobs_mapsFieldsWhenCredentialsPresent() throws IOException {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(loadFixture("adzuna-page1.json"), MediaType.APPLICATION_JSON));

        AdzunaProperties properties = new AdzunaProperties(BASE_URL, "de", "test-id", "test-key", 1);
        AdzunaAdapter adapter = new AdzunaAdapter(builder, properties);

        List<NormalizedJob> jobs = adapter.fetchJobs();

        assertThat(jobs).hasSize(2);
        NormalizedJob first = jobs.get(0);
        assertThat(first.getSource()).isEqualTo(JobSource.ADZUNA);
        assertThat(first.getExternalId()).isEqualTo("5001");
        assertThat(first.getTitle()).isEqualTo("Java Developer");
        assertThat(first.getCompany()).isEqualTo("Beta AG");
        assertThat(first.getLocation()).isEqualTo("Hamburg");
        assertThat(first.getCategory()).isEqualTo("IT Jobs");
        assertThat(first.getSalaryMin()).isEqualByComparingTo(BigDecimal.valueOf(55000));
        assertThat(first.getSalaryMax()).isEqualByComparingTo(BigDecimal.valueOf(70000));
        assertThat(first.getUrl()).isEqualTo("https://www.adzuna.de/details/5001");
        assertThat(first.getPublishedAt()).isNotNull();

        NormalizedJob second = jobs.get(1);
        assertThat(second.getSalaryMin()).isNull();
        assertThat(second.getSalaryMax()).isNull();

        server.verify();
    }

    private String loadFixture(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource("fixtures/" + name);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
