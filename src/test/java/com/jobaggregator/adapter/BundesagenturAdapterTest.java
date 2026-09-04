package com.jobaggregator.adapter;

import com.jobaggregator.config.BundesagenturProperties;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BundesagenturAdapterTest {

    private static final String BASE_URL =
            "https://rest.arbeitsagentur.de/jobboerse/jobsuche-service/pc/v6/jobs";

    @Test
    void fetchJobs_skipsWhenNoApiKey() {
        RestClient.Builder builder = RestClient.builder();
        BundesagenturProperties noKey = new BundesagenturProperties(BASE_URL, "", 5);
        BundesagenturAdapter adapter = new BundesagenturAdapter(builder, noKey);

        assertThat(adapter.fetchJobs()).isEmpty();
    }

    @Test
    void fetchJobs_mapsFieldsFromTreeModel() throws IOException {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(method(HttpMethod.GET))
                .andExpect(header("X-API-Key", "jobboerse-jobsuche"))
                .andRespond(withSuccess(loadFixture("bundesagentur-page1.json"), MediaType.APPLICATION_JSON));

        BundesagenturProperties properties =
                new BundesagenturProperties(BASE_URL, "jobboerse-jobsuche", 1);
        BundesagenturAdapter adapter = new BundesagenturAdapter(builder, properties);

        List<NormalizedJob> jobs = adapter.fetchJobs();

        assertThat(jobs).hasSize(2);

        NormalizedJob withExternalUrl = jobs.get(0);
        assertThat(withExternalUrl.getSource()).isEqualTo(JobSource.BUNDESAGENTUR);
        assertThat(withExternalUrl.getExternalId()).isEqualTo("10001-1234567-S");
        assertThat(withExternalUrl.getTitle()).isEqualTo("Softwareentwickler (m/w/d)");
        assertThat(withExternalUrl.getCompany()).isEqualTo("Gamma KG");
        assertThat(withExternalUrl.getLocation()).isEqualTo("Stuttgart");
        assertThat(withExternalUrl.getUrl()).isEqualTo("https://example.com/jobs/10001-1234567-S");
        assertThat(withExternalUrl.getPublishedAt()).isNotNull();

        NormalizedJob withoutExternalUrl = jobs.get(1);
        assertThat(withoutExternalUrl.getExternalId()).isEqualTo("20002-7654321-S");
        assertThat(withoutExternalUrl.getLocation()).isEqualTo("Leipzig");
        assertThat(withoutExternalUrl.getUrl()).isEqualTo(
                "https://www.arbeitsagentur.de/jobsuche/jobdetail/20002-7654321-S"
        );

        server.verify();
    }

    private String loadFixture(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource("fixtures/" + name);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
