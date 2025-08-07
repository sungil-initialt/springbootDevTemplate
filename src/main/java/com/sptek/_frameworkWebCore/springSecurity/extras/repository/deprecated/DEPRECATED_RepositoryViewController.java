package com.sptek._frameworkWebCore.springSecurity.extras.repository.deprecated;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.AuthorityDto;
import com.sptek._frameworkWebCore.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class DEPRECATED_RepositoryViewController {
    @NonFinal //생성자 주입 대상에서 제외
    private final String htmlBasePath = "pages/_example/html/";
    private final DEPRECATED_RepositoryService DEPRECATEDRepositoryService;

    //for test
    @GetMapping("/test/testRepo1/{key}")
    public String repoTest(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = DEPRECATEDRepositoryService.testRepository(key);
        model.addAttribute("result", resultMap);
        return htmlBasePath + "simpleModelView";
    }

    //for test
    @GetMapping("/test/testRepo2")
    public String testRepo2(Model model) {
        AuthorityEnum authority = AuthorityEnum.AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY;
        AuthorityDto authDto = ModelMapperUtil.map(authority, AuthorityDto.class);
        model.addAttribute("result", authDto);
        return htmlBasePath + "simpleModelView";
    }

}
