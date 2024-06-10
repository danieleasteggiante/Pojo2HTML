package src.main.java.it.netservice.engine;

import src.main.java.it.netservice.HTMLContent;
import src.main.java.it.netservice.annotations.HTMLTag;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 * @author Daniele Asteggiante
 */
class HTMLInjector {
    private static final Logger logger = Logger.getLogger(HTMLInjector.class.getName());
    HTMLContent content;
    StringBuilder htmlString = new StringBuilder();
    Map<String, HTMLTag> fieldNameToAnnotationMap = new LinkedHashMap<>();
    Map<String, Set<Field>> fieldsInArea = new LinkedHashMap<>();

    public HTMLInjector(HTMLContent content) {
        this.content = content;
        htmlString.append("<!DOCTYPE html>" + "<html lang=\"it\">" + "<head> @head@ </head>" + "<body>  @body@ </body>" + "</html>");
        searchFields();
        inject();
    }

    public void searchFields() {
        Field[] fields = content.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            HTMLTag htmlTagAnnotation = field.getAnnotation(HTMLTag.class);
            fieldNameToAnnotationMap.put(field.getName(), htmlTagAnnotation);
            addFieldInAreaMap(htmlTagAnnotation.area(), field);
        }
    }

    public void inject() {
        Set<String> keys = fieldsInArea.keySet();
        for (String k : keys) {
            try {
                if (k.equals("head"))
                    handleHeadFields(fieldsInArea.get(k));
                else
                    handleBodyFields(fieldsInArea.get(k));
            } catch (IllegalAccessException e) {
                logger.severe("Accesso alla classe non permesso");
            }
        }
    }

    private void handleBodyFields(Set<Field> fields) throws IllegalAccessException {
        for (Field f : fields) {
            HTMLTag htmlTagAnnotation = fieldNameToAnnotationMap.get(f.getName());
            if (htmlTagAnnotation.idWrapper().isEmpty())
                replacePlaceholderWithContent(f, "body");
            else
                handleBodyFieldWithIdWrapper(f, htmlTagAnnotation);
        }
    }

    private void handleBodyFieldWithIdWrapper(Field field, HTMLTag htmlTagAnnotation) throws IllegalAccessException {
        String idWrapper = htmlTagAnnotation.idWrapper();
        if (field.get(content) instanceof Collection) {
            handleCollectionField(field, idWrapper);
            return;
        }
        replacePlaceholderWithContent(field, idWrapper);
    }

    private void handleHeadFields(Set<Field> fields) throws IllegalAccessException {
        for (Field f : fields)
            replacePlaceholderWithContent(f, "head");
    }

    private void replacePlaceholderWithContent(Field field, String placeholder) throws IllegalAccessException {
        String fieldContent = (String) getFieldIfNotNull(field);
        String tagString = Tag.of(fieldNameToAnnotationMap.get(field.getName()));
        String tagStringWithContent = tagString.replace("@{content}@", fieldContent);
        StringBuilderUtils.replaceString("@" + placeholder + "@", tagStringWithContent + "@" + placeholder + "@", htmlString);
    }

    private void replacePlaceholderWithContent(String element, Field field, String idWrapper) {
        String tagStringWithContent = Tag.of(fieldNameToAnnotationMap.get(field.getName()));
        tagStringWithContent = tagStringWithContent.replace("@{content}@", element);
        StringBuilderUtils.replaceString("@" + idWrapper + "@", tagStringWithContent + " @" + idWrapper + "@", htmlString);
    }

    private void addFieldInAreaMap(String key, Field field) {
        if (fieldsInArea.containsKey(key))
            fieldsInArea.get(key).add(field);
        else {
            fieldsInArea.put(key, new LinkedHashSet<Field>());
            fieldsInArea.get(key).add(field);
        }
    }

    private Object getFieldIfNotNull(Field field) throws IllegalAccessException {
        Object value = field.get(content);
        if (value == null)
            throw new IllegalArgumentException(
                    "Il campo " + field.getName() + " non puo essere null, eliminarlo dalla classe JAVA o inizializzarlo");
        return value;
    }

    public String getHtmlString() {
        return htmlString.toString().replaceAll("@([^@]*)@", "");
    }

    private void handleCollectionField(Field field, String idWrapper) throws IllegalAccessException {
        if (idWrapper.isEmpty())
            throw new IllegalArgumentException("Il campo Collection deve avere un idWrapper");
        Collection<?> list = (Collection<?>) getFieldIfNotNull(field);
        for (Object o : list) {
            if (!(o instanceof String))
                throw new IllegalArgumentException("La collezione deve contenere solo stringhe");
            String element = (String) o;
            replacePlaceholderWithContent(element, field, idWrapper);
        }
    }
}
