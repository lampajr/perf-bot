package io.lampajr.service;

import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.data.ExportedLabelValues;
import io.hyperfoil.tools.horreum.api.data.LabelValueMap;
import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.hyperfoil.tools.horreum.api.services.RunService;
import io.lampajr.model.ProjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class HorreumService {

    @ConfigProperty(name = "horreum.gh.app.horreum.url")
    String horreumUrl;

    @Inject
    ConfigService configService;

    // TODO: get the run or the label values?
    public LabelValueMap getRun(String repo, String repositoryUrl, int horreumRunId) {
        check(repo, repositoryUrl);

        ProjectConfig config = configService.getConfig(repo);
        try (HorreumClient client = new HorreumClient.Builder().horreumUrl(horreumUrl).horreumApiKey(config.horreumKey)
                .build()) {
            List<ExportedLabelValues> labelValues = client.runService.getRunLabelValues(horreumRunId, null, null, null, 1000, 0,
                    null, null, false);
            // assuming we have one single dataset
            return labelValues.getFirst().values;
        }
    }

    /**
     * Compare the provided run against the baseline configured in Horreum
     * @param horreumRunId id of the run in Horreum
     */
    public List<ExperimentService.ExperimentResult> compare(String repo, String repositoryUrl, int horreumRunId) {
        check(repo, repositoryUrl);
        ProjectConfig config = configService.getConfig(repo);
        try (HorreumClient client = new HorreumClient.Builder().horreumUrl(horreumUrl).horreumApiKey(config.horreumKey)
                .build()) {
            RunService.RunExtended run = client.runService.getRun(horreumRunId);
            // assumption that there is only one dataset
            return client.experimentService.runExperiments(run.datasets[0]);
        }
    }

    private void check(String repo, String repositoryUrl) {
        if (!configService.getConfigs().containsKey(repo) || !configService.getConfig(repo).repository.equals(repositoryUrl)) {
            throw new RuntimeException("Trying to get data for test " + repo + " from " + repositoryUrl);
        }
    }
}
