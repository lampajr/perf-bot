package io.lampajr.service;

import io.hyperfoil.tools.horreum.api.data.LabelValueMap;
import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.lampajr.util.ExperimentResultConverter;
import io.lampajr.util.LabelValueMapConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class PullRequestService {

    @ConfigProperty(name = "horreum.gh.app.prompt")
    String prompt;

    @Inject
    HorreumService horreumService;

    @Inject
    LabelValueMapConverter labelValueMapConverter;

    @Inject
    ExperimentResultConverter experimentResultConverter;

    public void onGithubComment(GHEventPayload.IssueComment issueComment) throws IOException {
        // TODO: implement more robust parsing logic
        String[] comment = issueComment.getComment().getBody().split(" ");

        if (comment.length < 1 || !prompt.equalsIgnoreCase(comment[0])) {
            // skip comments that we are not interested in
            return;
        }

        String cmd = comment[1];
        String repo = issueComment.getIssue().getRepository().getFullName();
        String repoUrl = issueComment.getIssue().getRepository().getHtmlUrl().toString();
        String runId, commentResult = null;

        try {
            switch (cmd) {
                case "get": // get run label values
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum get <run-id>");
                    }
                    runId = comment[2];
                    LabelValueMap labelValues = horreumService.getRun(repo, repoUrl, Integer.parseInt(runId));
                    commentResult = "## Label values for run " + runId + "\n\n" + labelValueMapConverter.serialize(
                            labelValues);
                    break;
                case "trash":
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum trash <run-id>");
                    }
                    break;
                case "compare":
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum compare <run-id>");
                    }
                    runId = comment[2];
                    List<ExperimentService.ExperimentResult> comparisonResults = horreumService.compare(repo, repoUrl,
                            Integer.parseInt(runId));
                    commentResult = "## Comparison for " + runId + " against baseline" + "\n\n" + String.join("\n",
                            comparisonResults.stream()
                                    .map(experimentResult -> experimentResultConverter.serialize(experimentResult)).toList());
                    break;
                default:
                    issueComment.getIssue().comment("Unexpected command: " + cmd);
                    break;
            }

            if (commentResult != null) {
                issueComment.getIssue().comment(commentResult);
            }
        } catch (Exception e) {
            issueComment.getIssue().comment("Something went wrong processing cmd " + cmd);
        }
    }
}
