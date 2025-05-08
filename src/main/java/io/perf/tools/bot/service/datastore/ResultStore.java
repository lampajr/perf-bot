package io.perf.tools.bot.service.datastore;

public interface ResultStore {
    /**
     * Retrieve a specific run execution result
     * @param repo repository name
     * @param repositoryUrl repository url
     * @param runId run to compare
     * @return results as string
     */
    String getRun(String repo, String repositoryUrl, int runId);


    /**
     * Compare the specified run against a pre-defined baseline
     * @param repo repository name
     * @param repositoryUrl repository url
     * @param runId run to compare
     * @return comparison results as string
     */
    String compare(String repo, String repositoryUrl, int runId);
}
