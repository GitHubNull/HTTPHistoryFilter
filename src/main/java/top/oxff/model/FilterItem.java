package top.oxff.model;

public class FilterItem {
    private String method;
    private String path;
    private String httpVersion;

    public FilterItem() {
    }

    public FilterItem(String method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
