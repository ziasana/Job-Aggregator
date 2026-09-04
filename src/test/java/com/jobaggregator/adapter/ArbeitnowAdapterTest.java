package com.jobaggregator.adapter;

import com.jobaggregator.config.ArbeitnowProperties;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ArbeitnowAdapterTest {

    private static final String BASE_URL = "https://www.arbeitnow.com/api/job-board-api";

    @Test
    void fetchJobs_mapsFieldsAndStopsWhenNoNextLink() throws IOException {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(BASE_URL))
                .andRespond(withSuccess(loadFixture("arbeitnow-page1.json"), MediaType.APPLICATION_JSON));

        ArbeitnowAdapter adapter = new ArbeitnowAdapter(builder, new ArbeitnowProperties(BASE_URL, 5));

        List<NormalizedJob> jobs = adapter.fetchJobs();

        assertThat(jobs).hasSize(2);
        NormalizedJob first = jobs.get(0);
        assertThat(first.getSource()).isEqualTo(JobSource.ARBEITNOW);
        assertThat(first.getExternalId()).isEqualTo("backend-developer-acme-berlin");
        assertThat(first.getTitle()).isEqualTo("Backend Developer");
        assertThat(first.getCompany()).isEqualTo("Acme GmbH");
        assertThat(first.getLocation()).isEqualTo("Berlin");
        assertThat(first.getCategory()).isEqualTo("Backend");
        assertThat(first.getSalaryMin()).isNull();
        assertThat(first.getSalaryMax()).isNull();
        assertThat(first.getUrl()).isEqualTo("https://www.arbeitnow.com/view/backend-developer-acme-berlin");
        assertThat(first.getPublishedAt()).isNotNull();

        server.verify();
    }

    private String loadFixture(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource("fixtures/" + name);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
