package com.winus.express.common.annotation;

import java.lang.annotation.*;

/**
 * API 로깅을 비활성화하는 어노테이션
 * 민감한 정보가 포함된 API나 로깅이 필요없는 API에 사용
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoApiLogging {
}