/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TwitterRefValidator implements ConstraintValidator<TwitterRef , nl.vpro.domain.media.TwitterRef> {

    public static final Pattern PATTERN = Pattern.compile("^(@|#)[A-Za-z0-9_]{1,140}$");

    @Override
    public void initialize(TwitterRef twitterRef) {
    }

    @Override
    public boolean isValid(nl.vpro.domain.media.TwitterRef twitterRef, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if(twitterRef.getValue() == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }

        final Matcher matcher = PATTERN.matcher(twitterRef.getValue());
        if(!matcher.find()) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.twitterRefs.Pattern}")
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }

        if(twitterRef.getType() == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("type")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
