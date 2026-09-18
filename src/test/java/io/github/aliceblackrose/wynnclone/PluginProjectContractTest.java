package io.github.aliceblackrose.wynnclone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class PluginProjectContractTest {
    @Test
    void pluginDescriptorMatchesCombatEntrypoint() throws IOException {
        Map<String, String> descriptor = readPluginDescriptor();
        assertEquals("WynnClone", descriptor.get("name"));
        assertEquals("io.github.aliceblackrose.wynnclone.WynnClonePlugin", descriptor.get("main"));
        assertEquals("26.2", descriptor.get("api-version"));
    }

    private static Map<String, String> readPluginDescriptor() throws IOException {
        InputStream stream = PluginProjectContractTest.class.getClassLoader().getResourceAsStream("plugin.yml");
        assertNotNull(stream, "plugin.yml must be packaged on the test runtime classpath");

        Map<String, String> values = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> line.split(":", 2))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> values.put(parts[0].trim(), unquote(parts[1].trim())));
        }
        return values;
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
