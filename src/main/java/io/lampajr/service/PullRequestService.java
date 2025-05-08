package io.lampajr.service;

import io.hyperfoil.tools.horreum.api.data.LabelValueMap;
import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.lampajr.util.ExperimentResultConverter;
import io.lampajr.util.LabelValueMapConverter;
import io.quarkiverse.githubapp.runtime.github.PayloadHelper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class PullRequestService {

    @ConfigProperty(name = "horreum.gh.app.prompt")
    String prompt;

    @Inject
    HorreumService horreumService;

    @Inject
    JenkinsService jenkinsService;

    @Inject
    LabelValueMapConverter labelValueMapConverter;

    @Inject
    ExperimentResultConverter experimentResultConverter;

    public void onGithubComment(GHEventPayload.IssueComment issueComment) throws IOException {
        // TODO: implement more robust parsing logic
        String[] comment = issueComment.getComment().getBody().split(" ");

        if (comment.length < 1 || !prompt.equalsIgnoreCase(comment[0]) || !issueComment.getIssue().isPullRequest()) {
            // skip comments that we are not interested in
            return;
        }

        String cmd = comment[1];
        GHRepository repo = PayloadHelper.getRepository(issueComment);
        String repoFullName = repo.getFullName();
        String repoUrl = repo.getHtmlUrl().toString();
        String runId, commentResult = null;

        try {
            switch (cmd) {
                case "get": // get run label values
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum get <run-id>");
                    }
                    runId = comment[2];
                    LabelValueMap labelValues = horreumService.getRun(repoFullName, repoUrl, Integer.parseInt(runId));
                    commentResult = "## Label values for run " + runId + "\n\n" + labelValueMapConverter.serialize(
                            labelValues);
                    break;
                case "trash":
                    Log.error("Command not yet implemented");
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum trash <run-id>");
                    }
                    break;
                case "run":
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum run <job-id>");
                    }
                    // TODO: extract params from the comment
                    jenkinsService.buildJob(repoFullName, comment[2], new HashMap<>());
                    break;
                case "compare":
                    if (comment.length < 3) {
                        throw new IllegalArgumentException("Expecting at least 3 arguments, e.g., /horreum compare <run-id>");
                    }
                    runId = comment[2];
                    List<ExperimentService.ExperimentResult> comparisonResults = horreumService.compare(repoFullName, repoUrl,
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
            Log.error("Something went wrong", e);
            issueComment.getIssue().comment("Something went wrong processing cmd " + cmd + ": " + e.getMessage());
        }
    }
}
