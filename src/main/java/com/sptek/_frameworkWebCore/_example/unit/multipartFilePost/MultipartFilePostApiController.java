package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Multipart File", description = "")

public class MultipartFilePostApiController {
    private final MultipartFilePostService multipartFilePostService;

    @PostMapping(value = "/01/example/post/createPostWithFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) //미디어 타입 없어도 동작 하지만 미디어 타입별 동일 매핑이 있을 때는 반드시 추가
    @Operation(summary = "01. multipartFile 을 포함 하는 Form 데이터 처리", description = "")
    public Object multipartFilePost(@ModelAttribute ExamplePostDto examplePostDto, @RequestParam(required = false) List<MultipartFile> multipartFiles) throws Exception {
        return multipartFilePostService.createPost(examplePostDto, multipartFiles);
    }

    @GetMapping(value = "/02/example/post/fileByteFromStorage")
    public Object fileByteFromStorage(@RequestParam("requestFile") String requestFile)  throws Exception {
        return ResponseUtil.makeFileResponseEntityFromStorage(requestFile);
    }

//    @GetMapping(value = "/02/example/login/post/fileByteFromLoginUser")
//    public Object fileByteFromLoginUser(@RequestParam("requestFile") String requestFile)  throws Exception {
//        return ResponseUtil.makeFileResponseEntityFromLoginUser(requestFile);
//    }
//
//    @GetMapping(value = "/02/example/post/fileByteFromSpecificUser")
//    public Object fileByteFromSpecificUser(@RequestParam("requestFile") String requestFile)  throws Exception {
//        return ResponseUtil.makeFileResponseEntityFromSpecificUser(requestFile);
//    }
//
//    @GetMapping(value = "/02/example/post/fileByteFromSpecificRole")
//    public Object fileByteFromSpecificRole(@RequestParam("requestFile") String requestFile)  throws Exception {
//        return ResponseUtil.makeFileResponseEntityFromSpecificRole(requestFile);
//    }
//
//    @GetMapping(value = "/02/example/post/fileByteFromSpecificAuth")
//    public Object fileByteFromSpecificAuth(@RequestParam("requestFile") String requestFile)  throws Exception {
//        return ResponseUtil.makeFileResponseEntityFromSpecificAuth(requestFile);
//    }
}
