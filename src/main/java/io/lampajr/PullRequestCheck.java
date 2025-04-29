package io.lampajr;

import io.quarkiverse.githubapp.event.IssueComment;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHEventPayload;

@ApplicationScoped
public class PullRequestCheck {

    @ConfigProperty(name = "horreum.gh.app.prompt")
    String prompt;

    @Inject
    HorreumService horreumService;

    void onComment(@IssueComment GHEventPayload.IssueComment issueComment) {
        // TODO: implement more robust parsing logic
        String[] comment = issueComment.getComment().getBody().split(" ");
        Log.info("Checking comment " + issueComment.getComment().getBody());
        Log.info("Checking comment " + horreumService.getRun(comment[1], comment[2], Integer.parseInt(comment[3])));
    }
}
