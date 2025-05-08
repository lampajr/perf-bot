package io.lampajr.service;

import io.lampajr.model.ProjectConfig;
import io.lampajr.util.ResourceReader;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Startup
public class ConfigResolver {

    final Map<String, ProjectConfig> configs;

    public ConfigResolver() {
        configs = new HashMap<>();

        Yaml yaml = new Yaml();
        InputStream inputStream = ResourceReader.getResourceAsStream("config/repo-config.yaml");
        ProjectConfig config = yaml.loadAs(inputStream, ProjectConfig.class);
        configs.put(config.id, config);
    }

    public Map<String, ProjectConfig> getConfigs() {
        return configs;
    }

    public ProjectConfig getConfig(String repoFullName) {
        return configs.get(repoFullName);
    }
}
