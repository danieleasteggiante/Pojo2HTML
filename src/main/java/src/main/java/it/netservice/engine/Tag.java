package src.main.java.it.netservice.engine;


import src.main.java.it.netservice.annotations.HTMLTag;

/**
 * @author Daniele Asteggiante
 */
class Tag {
    private String id = "";
    private HTMLTag annotationTag;

    private Tag() {
    }

    public static String of(HTMLTag annotationTag) {
        Tag tag = new Tag();
        tag.annotationTag = annotationTag;
        return tag.render();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("<").append(annotationTag.name());
        if (annotationTag.attributes().length > 0)
            result.append(" ").append(renderAttributes(annotationTag.attributes()));
        if (annotationTag.autoClose())
            return result.append(" />").toString();
        result.append(">").append("@{content}@");
        if (!id.isEmpty())
            result.append(" @").append(id).append("@");
        result.append("</").append(annotationTag.name()).append(">");
        return result.toString();
    }

    private String renderAttributes(String[] attributes) {
        StringBuilder result = new StringBuilder();
        for (String attribute : attributes) {
            if (attribute.contains("id="))
                this.id = attribute.split("=")[1].replaceAll("'", "");
            result.append(attribute).append(" ");
        }
        return result.toString();
    }
}
