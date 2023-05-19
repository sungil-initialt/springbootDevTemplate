package com.sptek.webfw.support;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class InterceptorMatchSupport  implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;
    private final MatchInfoContainer matchInfoContainer;

    public InterceptorMatchSupport(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.matchInfoContainer = new MatchInfoContainer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (matchInfoContainer.notIncludedPath(request)) {
            return true;
        }

        return handlerInterceptor.preHandle(request, response, handler);
    }

    public InterceptorMatchSupport includePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    public InterceptorMatchSupport excludePathPattern(String pathPattern, HttpMethod pathMethod) {
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

        public boolean notIncludedPath(HttpServletRequest request) {
            boolean includeMatchResult = includeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

            boolean excludeMatchResult = excludeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

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



        public static void main(String[] args) {
            WebClient webClient = WebClient.create();

            // 첫 번째 RESTful 호출
            Mono<String> firstRequest = webClient.get()
                    .uri("https://api.github.com/users/octocat")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class);

            // 두 번째 RESTful 호출
            Mono<String> secondRequest = webClient.get()
                    .uri("https://api.github.com/users/octocat/repos")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class);

            // 비동기로 두 개의 호출 처리
            Mono.zip(firstRequest, secondRequest)
                    .doOnSuccess(response -> {
                        String firstResponse = response.getT1();
                        String secondResponse = response.getT2();
                        System.out.println("First Response: " + firstResponse);
                        System.out.println("Second Response: " + secondResponse);
                    })
                    .block();
        }

}


