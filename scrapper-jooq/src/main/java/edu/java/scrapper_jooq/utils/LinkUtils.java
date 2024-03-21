package edu.java.scrapper_jooq.utils;

import java.net.URI;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LinkUtils {
    public static String extractDomainFromUrl(URI url) {
        String textUrl = url.toString();

        String protocolSeparator = "://";
        int protocolIndex = textUrl.indexOf(protocolSeparator);

        if (protocolIndex != -1) {
            int startIndex = protocolIndex + protocolSeparator.length();
            String substring = textUrl.substring(startIndex);

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
