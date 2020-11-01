package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExComponentFilter implements ComponentFilter {
    private static final Logger log = LoggerFactory.getLogger(RegExComponentFilter.class);

    private final List<Pattern> patterns = new ArrayList<>();

    public RegExComponentFilter(String[] regExs) {
        for (String regEx : regExs) {
            patterns.add(Pattern.compile(regEx));
        }
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        if (patterns.isEmpty()) {
            return true;
        }
        return doEvaluateMatch(classMetadata);
    }

    private boolean doEvaluateMatch(ClassMetadata classMetadata) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(classMetadata.getClassName());
            if (matcher.matches()) {
                log.debug("Match found. Class: {} matched regex: {}", classMetadata.getClassName(), pattern.pattern());
                return true;
            }
        }
        return false;
    }
}
