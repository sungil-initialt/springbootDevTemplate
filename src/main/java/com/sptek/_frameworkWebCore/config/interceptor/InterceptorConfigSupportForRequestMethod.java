package com.sptek._frameworkWebCore.config.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

/*/
인터셉터가 특정 메소드(GET, POST, PUT, DELETE 등)를 구분해서 동작해야 하는 경우 InterceptorMatchSupport 를 통해 해당 인터셉터를 등록하도록 한다.
물론!! 인터셉터 내부에서 request.getMetho() 를 통해 구분된 동작을 처리할 수 있으나 인터셉터는 별도의 config 파일에 동작을 명시하고 있는 방식임으로 일관성을 갖게 처리하기 위함
 */
@Slf4j
public class InterceptorConfigSupportForRequestMethod implements HandlerInterceptor {
    private final HandlerInterceptor handlerInterceptor;
    private final MatchInfoContainer matchInfoContainer;

    public InterceptorConfigSupportForRequestMethod(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.matchInfoContainer = new MatchInfoContainer();
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (matchInfoContainer.isMatchedRequest(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    //실제 사용할 경우가 있을까?
    public InterceptorConfigSupportForRequestMethod includePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    public InterceptorConfigSupportForRequestMethod excludePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.excludePathPattern(pathPattern, pathMethod);
        return this;
    }

    @Data
    @AllArgsConstructor
    public class MatchInfo {
        private String path;
        private HttpMethod method;
    }

    public class MatchInfoContainer {
        private final AntPathMatcher pathMatcher;
        private final List<MatchInfo> includeMatchInfos;
        private final List<MatchInfo> excludeMatchInfos;

        public MatchInfoContainer() {
            this.pathMatcher = new AntPathMatcher();
            this.includeMatchInfos = new ArrayList<>();
            this.excludeMatchInfos = new ArrayList<>();
        }

        public boolean isMatchedRequest(HttpServletRequest request) {
            boolean includeMatchResult = includeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

            boolean excludeMatchResult = excludeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

            log.debug("includeMatchResult={}, excludeMatchResult={}", includeMatchResult, excludeMatchResult);
            return includeMatchResult || !excludeMatchResult;
        }

        private boolean anyMatchPathPattern(HttpServletRequest request, MatchInfo matchInfo) {
            return pathMatcher.match(matchInfo.getPath(), request.getServletPath()) &&
                    matchInfo.getMethod().matches(request.getMethod());
        }

        public void includePathPattern(String includePath, HttpMethod includeMethod) {
            this.includeMatchInfos.add(new MatchInfo(includePath, includeMethod));
        }

        public void excludePathPattern(String excludePath, HttpMethod excludeMethod) {
            this.excludeMatchInfos.add(new MatchInfo(excludePath, excludeMethod));
        }
    }

}


