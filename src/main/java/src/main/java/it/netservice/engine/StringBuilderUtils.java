package src.main.java.it.netservice.engine;

/**
 * @author Daniele Asteggiante
 */
class StringBuilderUtils {
    static void replaceString(String toReplace, String replacement, StringBuilder destination) {
        int startIndex = destination.indexOf(toReplace);
        if (startIndex == -1)
            throw new IllegalArgumentException("Stringa da sostituire non trovata controllare che l'elemento da cercare sia corretto");
        while (startIndex != -1) {
            int endIndex = startIndex + toReplace.length();
            destination.replace(startIndex, endIndex, replacement);
            startIndex = destination.indexOf(toReplace, startIndex + replacement.length());
        }
    }
}
