package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Multipart File", description = "")

public class MultipartFilePostApiController {



    @PostMapping("/01/example/multipartFile/multipartFilePost")
    @Operation(summary = "01. multipartFile 을 포함 하는 Form 데이터 처리", description = "")
    public Object allTypeEncryptForString(@ModelAttribute ExamplePostDto examplePostDto) {
        return examplePostDto;
    }

}
