/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.user.PortalService;
import nl.vpro.domain.user.ServiceLocator;


/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Slf4j
public class PortalValidator implements ConstraintValidator<ValidPortal, String> {


    @Override
    public void initialize(ValidPortal constraintAnnotation) {

    }

    @Override
    public boolean isValid(String portalId, ConstraintValidatorContext context) {
        if (portalId == null) {
            return true;
        }
        PortalService portalService = ServiceLocator.getPortalService();
        if (portalService == null) {
            log.warn("No portal service found");
            return false;
        }
        return portalService.find(portalId) != null;
    }

}
