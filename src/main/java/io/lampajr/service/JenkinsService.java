package io.lampajr.service;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import io.lampajr.model.JobDef;
import io.lampajr.model.ProjectConfig;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class JenkinsService {

    @Inject
    ConfigResolver configResolver;

    @Inject
    JenkinsServer jenkinsServer;

    public void buildJob(String repoFullName, String jobId, Map<String, String> params) {
        // TODO: implement
        ProjectConfig config = configResolver.getConfig(repoFullName);
        JobDef jobDef = config.jobs.get(jobId);

        try {
            JobWithDetails jenkinsJob = jenkinsServer.getJob(jobDef.jenkinsJob);

            QueueReference queueReference;
            if (params.isEmpty()) {
                queueReference = jenkinsJob.build(Map.of());
            } else {
                queueReference = jenkinsJob.build(params);
            }
            Log.info("Building job " + jobDef.name + " " + queueReference.getQueueItemUrlPart());
        } catch (Exception e) {
            Log.error("Something went wrong", e);
            throw new RuntimeException(e);
        }
        Log.info("build completed");
    }
}
