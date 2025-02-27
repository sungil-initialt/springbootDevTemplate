package com.sptek._frameworkWebCore.springSecurity.extras.repository.test;

import com.sptek._frameworkWebCore.annotation.EnableViewCommonErrorResponse;
import com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum;
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
@EnableViewCommonErrorResponse
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class RepositoryViewController {
    @NonFinal //생성자 주입 대상에서 제외
    private final String pageBasePath = "pages/example/test/";
    private final RepositoryService repositoryService;

    //for test
    @GetMapping("/user/test/{key}")
    public String test(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = repositoryService.testRepository(key);
        model.addAttribute("result", resultMap);
        return pageBasePath + "simpleModelView";
    }

    //for test
    @GetMapping("/user/test")
    public String test(Model model) {
        AuthorityIfEnum authority = AuthorityIfEnum.AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY;
        AuthorityDto authDto = ModelMapperUtil.map(authority, AuthorityDto.class);
        model.addAttribute("result", authDto);
        return pageBasePath + "simpleModelView";
    }

}
