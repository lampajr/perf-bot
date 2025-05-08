package io.perf.tools.bot.service.datastore.horreum.util;

import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.perf.tools.bot.util.Converter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExperimentResultConverter implements Converter<ExperimentService.ExperimentResult> {

    @Override
    public String serialize(ExperimentService.ExperimentResult value) {
        StringBuilder builder = new StringBuilder();

        builder.append("### Comparison ").append(value.profile.name).append("\n\n");

        builder.append("| Metric | Value | Baseline | Result |").append("\n");
        builder.append("| ------ |:-----:|:--------:|:------:|").append("\n");

        value.results.forEach((key, res) -> builder.append("| ").append(key)
                .append(" | ").append(res.experimentValue)
                .append(" | ").append(res.baselineValue)
                .append(" | <span style='color:blue'>").append(res.result).append("</span>")
                .append(" |").append("\n"));

        builder.append("\n");

        return builder.toString();
    }
}
