package io.lampajr.handler.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lampajr.service.PullRequestService;
import io.quarkiverse.githubapp.runtime.github.GitHubService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestHeader;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;
import java.io.StringReader;

@Path("/github")
public class GithubWebhookResource {

    @ConfigProperty(name = "horreum.gh.app.installation.id")
    Long installationId;

    @Inject
    GitHubService gitHubService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PullRequestService pullRequestService;

    @POST
    @ResponseStatus(204)
    @Produces(MediaType.APPLICATION_JSON)
    // TODO(user): implement validation https://docs.github.com/en/webhooks/using-webhooks/securing-your-webhooks
    public void webhook(@RestHeader("X-Github-Event") String event, @RestHeader("X-GitHub-Hook-ID") String hookId,
            ObjectNode payload) throws IOException {
        GHEventPayload.IssueComment issueComment = gitHubService.getInstallationClient(installationId)
                .parseEventPayload(new StringReader(objectMapper.writeValueAsString(payload)),
                        GHEventPayload.IssueComment.class);

        pullRequestService.onGithubComment(issueComment);
    }
}
