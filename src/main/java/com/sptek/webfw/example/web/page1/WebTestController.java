package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.exception.ApiBusinessException;
import com.sptek.webfw.code.ApiErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class WebTestController {

    @RequestMapping("/welcome")
    public String welcome(Model model) {
        log.debug("called welcome");

        model.addAttribute("message", "welcome");
        if(1==1)
            throw new ApiBusinessException(ApiErrorCode.BUSINESS_DEFAULT_ERROR, "welcome");

        return "welcome";
    }

}
