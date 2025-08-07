package com.sptek._frameworkWebCore.actuator;

import com.sptek._frameworkWebCore.commonObject.vo.ProjectInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component

public class CustomInfoContributor implements InfoContributor {
    private final ProjectInfoVo projectInfoVo;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", projectInfoVo.getApp().getName(),
                "version", projectInfoVo.getApp().getVersion(),
                "description", projectInfoVo.getApp().getDescription()
        ));
    }
}