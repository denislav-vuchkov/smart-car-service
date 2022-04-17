package com.smart.garage.controllers.mvc;

import com.smart.garage.models.User;
import com.smart.garage.utility.AuthenticationHelper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice("com.smart.garage.controllers.mvc")
public class AnnotationAdvice {

    private final AuthenticationHelper authenticationHelper;

    public AnnotationAdvice(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated() {
        return authenticationHelper.isAuthenticated();
    }

    @ModelAttribute("currentUser")
    public User populateIsAdmin() {
        return authenticationHelper.getCurrentUser();
    }

}
