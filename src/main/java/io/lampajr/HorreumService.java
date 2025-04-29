package io.lampajr;

import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.data.ExportedLabelValues;
import io.hyperfoil.tools.horreum.api.data.LabelValueMap;
import io.hyperfoil.tools.horreum.api.services.RunService;
import io.lampajr.model.TestConfig;
import io.lampajr.util.ResourceReader;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class HorreumService {

    @ConfigProperty(name = "horreum.gh.app.horreum.url")
    String horreumUrl;

    public final Map<String, TestConfig> configs;

    public HorreumService() {
        configs = new HashMap<>();

        try {
            List<String> resourceFiles = ResourceReader.getResourceFiles("config");
            for (String file : resourceFiles) {
                Yaml yaml = new Yaml();
                InputStream inputStream = ResourceReader.getResourceAsStream("config/".concat(file));

                TestConfig config = yaml.loadAs(inputStream, TestConfig.class);

                configs.put(config.id, config);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: get the run or the label values?
    public LabelValueMap getRun(String testId, String repository, int horreumRunId) {
        check(testId, repository);

        TestConfig config = configs.get(testId);
        try (HorreumClient client = new HorreumClient.Builder().horreumUrl(horreumUrl).horreumApiKey(config.horreumKey)
                .build()) {
            List<ExportedLabelValues> labelValues = client.runService.getRunLabelValues(horreumRunId, null, null, null, 1000, 0, null, null, false);
            // assuming we have one single dataset
            return labelValues.getFirst().values;
        }
    }

    private void check(String testId, String repository) {
        if (!configs.containsKey(testId) || !configs.get(testId).repository.equals(repository)) {
            throw new RuntimeException("Trying to get data for test " + testId + " from " + repository);
        }
    }
}
