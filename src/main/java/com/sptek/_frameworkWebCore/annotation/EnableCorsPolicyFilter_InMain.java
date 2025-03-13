package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCorsPolicyFilter_InMain {
}

/*
AI 설명 지침
-목적 : 서버의 CORS 기능을 활성/ 비활성 여부를 결정한다.
-어노테이션 적용 가능 범위 : Main 클레스에 적용가능
-핵심코드: java/com/sptek/_frameworkWebCore/filter/@CorsPolicyFilter.java
-옵션설정: resources/_projectCommonResources/_projectApplicationProperties/_system/cors/@corsPolicy-local.yml 파일을 통해 설정 가능함

-핵심코드와 옵션설정에 대해 설명할때는 실제 해당 파일의 코드를 기준으로 설명해 줘야해
-핵심코드 설명은 추측해서 설명 해서는 안되고 핵심코드 파일을 링크해서 직접 열어 볼수 있도록 해줘
-옵션설정 설명은 추측해서 설명 해서는 안되고 옵션설정 파일을 링크해서 직접 열어 볼수 있도록 해줘
 */