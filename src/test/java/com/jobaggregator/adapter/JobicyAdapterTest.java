package com.jobaggregator.adapter;

import com.jobaggregator.config.JobicyProperties;
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

class JobicyAdapterTest {

    private static final String BASE_URL = "https://jobicy.com/api/v2/remote-jobs";

    @Test
    void fetchJobs_mapsFields() throws IOException {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(loadFixture("jobicy-page1.json"), MediaType.APPLICATION_JSON));

        JobicyProperties properties = new JobicyProperties(BASE_URL, 200);
        JobicyAdapter adapter = new JobicyAdapter(builder, properties);

        List<NormalizedJob> jobs = adapter.fetchJobs();

        assertThat(jobs).hasSize(2);

        NormalizedJob withSalary = jobs.get(0);
        assertThat(withSalary.getSource()).isEqualTo(JobSource.JOBICY);
        assertThat(withSalary.getExternalId()).isEqualTo("150923");
        assertThat(withSalary.getTitle()).isEqualTo("Data Analyst, Clinical Data Effectiveness");
        assertThat(withSalary.getCompany()).isEqualTo("Clover Health");
        assertThat(withSalary.getLocation()).isEqualTo("USA");
        assertThat(withSalary.getSalaryMin()).isEqualByComparingTo(BigDecimal.valueOf(77000));
        assertThat(withSalary.getSalaryMax()).isEqualByComparingTo(BigDecimal.valueOf(100000));
        assertThat(withSalary.getCurrency()).isEqualTo("USD");
        assertThat(withSalary.getUrl()).isEqualTo(
                "https://jobicy.com/jobs/150923-data-analyst-clinical-data-effectiveness"
        );
        assertThat(withSalary.getPublishedAt()).isNotNull();

        NormalizedJob withoutSalary = jobs.get(1);
        assertThat(withoutSalary.getExternalId()).isEqualTo("150210");
        assertThat(withoutSalary.getSalaryMin()).isNull();
        assertThat(withoutSalary.getSalaryMax()).isNull();

        server.verify();
    }

    private String loadFixture(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource("fixtures/" + name);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
