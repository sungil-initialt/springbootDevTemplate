package com.sptek.webfw.support;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

/*/
인터셉터가 특정 메소드(GET, POST, PUT, DELETE 등)를 구분해서 동작해야 하는 경우 InterceptorMatchSupport 를 통해 해당 인터셉터를 등록하도록 한다.
 */
@Slf4j
public class MethodCheckInterceptorSupport implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;
    private final MatchInfoContainer matchInfoContainer;

    public MethodCheckInterceptorSupport(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.matchInfoContainer = new MatchInfoContainer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (matchInfoContainer.isMatchedRequest(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    //사용할 경우가 있을까?
    public MethodCheckInterceptorSupport includePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    public MethodCheckInterceptorSupport excludePathPattern(String pathPattern, HttpMethod pathMethod) {
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


