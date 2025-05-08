package io.perf.tools.bot.model;

import java.util.Map;

public class ProjectConfig {
    public String id;
    public String repository;
    public String horreumTestId;
    public String description;
    // full path of the script to run
    public String horreumKey;
    public Map<String, JobDef> jobs;
}
