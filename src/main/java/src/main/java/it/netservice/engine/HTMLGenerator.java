package src.main.java.it.netservice.engine;


import src.main.java.it.netservice.HTMLContent;

/**
 * @author Daniele Asteggiante
 */
public class HTMLGenerator {
    public static String of(HTMLContent content) {
        HTMLInjector injector = new HTMLInjector(content);
        return injector.getHtmlString();
    }
}
