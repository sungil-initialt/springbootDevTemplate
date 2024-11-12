package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.AuthorityEnum;
import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.util.ModelMapperUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class TestController {
    private final String pagePath = "pages/example/test/";
    @Autowired
    private TestService testService;

    //for test
    @GetMapping("/user/test/{key}")
    public String test(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = testService.testRepository(key);
        model.addAttribute("result", resultMap);
        return pagePath + "simpleModelView";
    }

    //for test
    @GetMapping("/user/test")
    public String test(Model model) {
        AuthorityEnum authority = AuthorityEnum.AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY;
        AuthorityDto authDto = ModelMapperUtil.map(authority, AuthorityDto.class);
        model.addAttribute("result", authDto);
        return pagePath + "simpleModelView";
    }

}
