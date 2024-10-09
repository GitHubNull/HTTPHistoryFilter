package top.oxff.model;

public enum MarkType {
    NOTE("note"),
    HIGHLIGHT("highlight");

    private final String type;

    MarkType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
}
