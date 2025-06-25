package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
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

//    @PostMapping("/01/example/multipartFile/multipartFilePost")
//    @Operation(summary = "01. multipartFile 을 포함 하는 Form 데이터 처리", description = "")
//    public Object multipartFilePost(@ModelAttribute PostExDto postExDto, @RequestParam(required = false) MultipartFile[] multipartFiles) throws Exception {
//        return multipartFilesUploadServiceForEx.composeInsertJobs(postExDto, multipartFiles);
//    }

    @PostMapping("/01/example/multipartFile/createFilePost")
    @Operation(summary = "01. multipartFile 을 포함 하는 Form 데이터 처리", description = "")
    public Object multipartFilePost(@ModelAttribute ExamplePostDto examplePostDto, @RequestParam(required = false) List<MultipartFile> multipartFiles) throws Exception {
        return multipartFilePostService.createPost(examplePostDto, multipartFiles);
    }
}
