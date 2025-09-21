package com.winus.express.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winus.express.common.annotation.NoApiLogging;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * API 요청/응답 로깅 AOP
 * 모든 컨트롤러의 요청과 응답을 로깅합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiLoggingAspect {

    private final ObjectMapper objectMapper;

    /**
     * 모든 컨트롤러 패키지의 메소드를 포인트컷으로 지정
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {
    }

    /**
     * 모든 @RequestMapping 어노테이션이 붙은 메소드를 포인트컷으로 지정
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingMethods() {
    }

    /**
     * API 요청/응답 로깅
     */
    @Around("restControllerMethods() && requestMappingMethods()")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        // @NoApiLogging 어노테이션 체크
        if (isNoLogging(joinPoint)) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        long startTime = System.currentTimeMillis();

        // 요청 정보 로깅
        logRequest(request, joinPoint);

        try {
            // 실제 메소드 실행
            Object result = joinPoint.proceed();

            // 응답 정보 로깅
            logResponse(request, result, System.currentTimeMillis() - startTime);

            return result;
        } catch (Exception e) {
            // 에러 정보 로깅
            logError(request, e, System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    /**
     * 요청 정보 로깅
     */
    private void logRequest(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        try {
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("method", request.getMethod());
            requestInfo.put("uri", request.getRequestURI());
            requestInfo.put("queryString", request.getQueryString());
            requestInfo.put("remoteAddr", getClientIpAddress(request));
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("controller", joinPoint.getSignature().getDeclaringTypeName());
            requestInfo.put("methodName", joinPoint.getSignature().getName());

            // 헤더 정보 (민감한 정보는 제외)
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!headerName.equalsIgnoreCase("Authorization") &&
                    !headerName.equalsIgnoreCase("Cookie")) {
                    headers.put(headerName, request.getHeader(headerName));
                }
            }
            requestInfo.put("headers", headers);

            // 요청 파라미터
            if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
                requestInfo.put("parameters", Arrays.toString(joinPoint.getArgs()));
            }

            log.info("========== API Request ==========");
            log.info("Request Info: {}", objectMapper.writeValueAsString(requestInfo));
        } catch (Exception e) {
            log.error("Error logging request: ", e);
        }
    }

    /**
     * 응답 정보 로깅
     */
    private void logResponse(HttpServletRequest request, Object result, long executionTime) {
        try {
            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("uri", request.getRequestURI());
            responseInfo.put("method", request.getMethod());
            responseInfo.put("executionTime", executionTime + "ms");
            responseInfo.put("status", "SUCCESS");

            // 응답 데이터 (너무 크지 않은 경우에만)
            if (result != null) {
                String resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() <= 1000) {
                    responseInfo.put("response", result);
                } else {
                    responseInfo.put("response", "Response too large to log (size: " + resultStr.length() + ")");
                }
            }

            log.info("========== API Response ==========");
            log.info("Response Info: {}", objectMapper.writeValueAsString(responseInfo));
        } catch (Exception e) {
            log.error("Error logging response: ", e);
        }
    }

    /**
     * 에러 정보 로깅
     */
    private void logError(HttpServletRequest request, Exception exception, long executionTime) {
        try {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("uri", request.getRequestURI());
            errorInfo.put("method", request.getMethod());
            errorInfo.put("executionTime", executionTime + "ms");
            errorInfo.put("status", "ERROR");
            errorInfo.put("errorMessage", exception.getMessage());
            errorInfo.put("errorType", exception.getClass().getSimpleName());

            log.error("========== API Error ==========");
            log.error("Error Info: {}", objectMapper.writeValueAsString(errorInfo));
            log.error("Stack Trace: ", exception);
        } catch (Exception e) {
            log.error("Error logging error: ", e);
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * @NoApiLogging 어노테이션 체크
     */
    private boolean isNoLogging(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 메소드에 @NoApiLogging이 있는지 체크
        if (method.isAnnotationPresent(NoApiLogging.class)) {
            return true;
        }

        // 클래스에 @NoApiLogging이 있는지 체크
        return targetClass.isAnnotationPresent(NoApiLogging.class);
    }
}