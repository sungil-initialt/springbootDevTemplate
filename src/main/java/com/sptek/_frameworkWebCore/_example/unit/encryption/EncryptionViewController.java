package com.sptek._frameworkWebCore._example.unit.encryption;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
@EnableResponseOfViewGlobalException_InViewController

public class EncryptionViewController {
}
