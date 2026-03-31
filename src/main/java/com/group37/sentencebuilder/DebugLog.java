package com.group37.sentencebuilder;

import java.io.FileWriter;
import java.util.Map;

/**
 * Debug-mode NDJSON logger (session 4d7069). Do not log secrets.
 */
public final class DebugLog {

    private static final String PATH = "/Users/hnnong/group-37/.cursor/debug-4d7069.log";
    private static final String SESSION = "4d7069";

    // #region agent log
    public static void agent(String runId, String hypothesisId, String location, String message, Map<String, String> data) {
        try {
            StringBuilder sb = new StringBuilder(320);
            sb.append("{\"sessionId\":\"").append(SESSION).append("\",\"runId\":\"").append(escape(runId));
            sb.append("\",\"hypothesisId\":\"").append(escape(hypothesisId)).append("\",\"location\":\"").append(escape(location));
            sb.append("\",\"message\":\"").append(escape(message)).append("\",\"timestamp\":").append(System.currentTimeMillis());
            if (data != null && !data.isEmpty()) {
                sb.append(",\"data\":{");
                boolean first = true;
                for (var e : data.entrySet()) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    sb.append('"').append(escape(e.getKey())).append("\":\"").append(escape(e.getValue())).append('"');
                }
                sb.append('}');
            }
            sb.append("}\n");
            try (FileWriter fw = new FileWriter(PATH, true)) {
                fw.write(sb.toString());
            }
        } catch (Exception ignored) {
        }
    }
    // #endregion

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private DebugLog() {
    }
}
