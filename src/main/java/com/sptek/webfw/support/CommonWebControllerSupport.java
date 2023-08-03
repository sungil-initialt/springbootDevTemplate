package com.sptek.webfw.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class CommonWebControllerSupport {
    @Autowired
    private Environment environment;
}
