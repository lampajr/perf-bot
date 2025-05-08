package io.perf.tools.bot.service.job;

import java.util.Map;

public interface JobRunner {
    void buildJob(String repoFullName, String jobId, Map<String, String> params);
}
