package com.sptek._frameworkWebCore._example.unit.LocaleLanguage;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import com.sptek._frameworkWebCore.util.LocaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Controller
@EnableResponseOfViewGlobalException_InViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class LocaleLanguageViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @RequestMapping("/localeLanguage/myLocaleLanguage")
    public String i18n(Model model) throws Exception {
        ZonedDateTime zonedDateTimeForSystem = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime zonedDateTimeForUser = ZonedDateTime.now(LocaleUtil.getCurTimeZone().toZoneId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String systemFormattedDateTime = zonedDateTimeForSystem.format(dateTimeFormatter);
        String userFormattedDateTime = zonedDateTimeForUser.format(dateTimeFormatter);

        String userLanguageTag = LocaleUtil.getCurLanguageTag();
        String userTimeZone = LocaleUtil.getCurTimeZoneName();

        String language = LocaleUtil.getI18nMessage("language");
        //Controller 에서 다국어 변환을 직접 하는 케이스
        String welcome = LocaleUtil.getI18nMessage("welcome"
                , new Object[] {AuthenticationUtil.getMyName()
                        , AuthenticationUtil.getMyRoles().toString()});

        model.addAttribute("userLanguageTag", userLanguageTag);
        model.addAttribute("userTimeZone", userTimeZone);

        model.addAttribute("language", language);
        model.addAttribute("welcome", welcome);

        model.addAttribute("systemFormattedDateTime", systemFormattedDateTime);
        model.addAttribute("userFormattedDateTime", userFormattedDateTime);

        return htmlBasePath + "localeLanguage";
    }
}