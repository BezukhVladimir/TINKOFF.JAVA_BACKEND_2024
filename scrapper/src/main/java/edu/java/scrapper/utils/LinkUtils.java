package edu.java.scrapper.utils;


public class LinkUtils {
    private LinkUtils() {}

    public static String extractDomainFromUrl(String url) {
        String protocolSeparator = "://";
        int protocolIndex = url.indexOf(protocolSeparator);

        if (protocolIndex != -1) {
            int startIndex = protocolIndex + protocolSeparator.length();
            String substring = url.substring(startIndex);

            int index = substring.indexOf("/");
            if (index != -1) {
                return substring.substring(0, index);
            } else {
                return substring;
            }
        }

        return "";
    }
}
