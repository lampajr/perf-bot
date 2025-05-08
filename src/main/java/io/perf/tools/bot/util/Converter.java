package io.perf.tools.bot.util;

/**
 * Serialize object to String properly formatted to be
 * inlined into a GitHub comment
 * @param <T>
 */
public interface Converter<T> {

    String serialize(T value);
}
