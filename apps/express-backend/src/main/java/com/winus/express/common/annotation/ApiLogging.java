package com.winus.express.common.annotation;

import java.lang.annotation.*;

/**
 * API 로깅 레벨을 제어하는 어노테이션
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLogging {

    /**
     * 로깅 레벨
     */
    LogLevel level() default LogLevel.FULL;

    /**
     * 요청 로깅 여부
     */
    boolean logRequest() default true;

    /**
     * 응답 로깅 여부
     */
    boolean logResponse() default true;

    /**
     * 실행 시간 로깅 여부
     */
    boolean logExecutionTime() default true;

    /**
     * 헤더 로깅 여부
     */
    boolean logHeaders() default true;

    /**
     * 파라미터 로깅 여부
     */
    boolean logParameters() default true;

    /**
     * 로깅 레벨 열거형
     */
    enum LogLevel {
        BASIC,   // 기본 정보만 (URI, Method, 실행시간)
        HEADERS, // 헤더 포함
        FULL     // 모든 정보 포함
    }
}