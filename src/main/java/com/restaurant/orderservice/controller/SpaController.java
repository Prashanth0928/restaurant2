package com.restaurant.orderservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Forwards all unknown GET routes to index.html so React handles its own routing.
 * REST controllers (/orders, /meals, etc.) take precedence over this catch-all.
 */
@Controller
public class SpaController {

    @RequestMapping(value = "/{path:[^\\.]*}", method = RequestMethod.GET)
    public String forward() {
        return "forward:/index.html";
    }
}
