package com.nekretninanet.backend.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SanitizeUtil {

    private static final Logger logger = LoggerFactory.getLogger(SanitizeUtil.class);

    private static final PolicyFactory POLICY =
            Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);

    private SanitizeUtil() {
        // spriječava instanciranje
    }

    public static String sanitize(String input) {

        if (input == null || input.isBlank()) {
            return input;
        }

        try {
            String sanitized = POLICY.sanitize(input);

            if (!input.equals(sanitized)) {
                logger.warn("Detektovan i uklonjen potencijalno opasan HTML/XSS sadržaj.");
            }

            return sanitized;

        } catch (Exception e) {
            logger.error("Greška tokom sanitizacije inputa", e);
            return ""; // sigurnije nego vratiti nesanitizovan input
        }
    }
}