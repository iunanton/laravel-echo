package me.edgeconsult.laravelecho.util;

public final class EventFormatter {
    private String namespace;

    public EventFormatter() {
    }

    public EventFormatter(String namespace) {
        this.namespace = namespace;
    }

    public String format(String event) {
        String result = event;
        if (result.charAt(0) == '.' || result.charAt(0) == '\\') {
            return result.substring(1);
        } else if (!namespace.isEmpty()) {
            result = namespace + "." + event;
        }
        return result.replace('.', '\\');
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
