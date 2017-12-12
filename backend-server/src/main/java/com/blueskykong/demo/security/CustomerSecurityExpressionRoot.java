package com.blueskykong.demo.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;


/**
 * Base root object for use in Spring Security expression evaluations.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class CustomerSecurityExpressionRoot extends SecurityExpressionRoot {

    public CustomerSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }
}
