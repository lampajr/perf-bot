package io.lampajr.util;

import io.hyperfoil.tools.horreum.api.data.LabelValueMap;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LabelValueMapConverter implements Converter<LabelValueMap> {

    /**
     * Convert the {@link LabelValueMap} into a Markdown table representation
     * @param value label values map
     * @return Table markdown in String format
     */
    @Override
    public String serialize(LabelValueMap value) {
        StringBuilder builder = new StringBuilder();

        builder.append("| Metric | Value |").append("\n");
        builder.append("| ------ |:-----:|").append("\n");

        value.forEach((key, value1) -> builder.append("| ").append(key).append(" | ").append(value1).append(" |")
                .append("\n"));

        builder.append("\n");

        return builder.toString();
    }
}
