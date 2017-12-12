package com.blueskykong.gateway.properties;


import lombok.Data;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author keets
 * @date 2017/9/25
 */
@Data
public class PermitAllUrlProperties {

    private static List<Pattern> permitallUrlPattern;

    private List<url> permitall;

    public String[] getPermitallPatterns() {
        List<String> urls = new ArrayList();
        Iterator<url> iterator = permitall.iterator();
        while (iterator.hasNext()) {
            urls.add(iterator.next().getPattern());
        }
        return urls.toArray(new String[0]);
    }

    public static List<Pattern> getPermitallUrlPattern() {
        return permitallUrlPattern;
    }

    public static class url {
        private String pattern;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    @PostConstruct
    public void init() {
        if (permitall != null && permitall.size() > 0) {
            this.permitallUrlPattern = new ArrayList();
            Iterator<url> iterator = permitall.iterator();
            while (iterator.hasNext()) {
                String currentUrl = iterator.next().getPattern().replaceAll("\\*\\*", "(.*?)");
                Pattern currentPattern = Pattern.compile(currentUrl, Pattern.CASE_INSENSITIVE);
                permitallUrlPattern.add(currentPattern);
            }

        }
    }

    public boolean isPermitAllUrl(String url) {
        for (Pattern pattern : permitallUrlPattern) {
            if (pattern.matcher(url).find()) {
                return true;
            }
        }
        return false;
    }
}

