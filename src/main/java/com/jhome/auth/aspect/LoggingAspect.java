package com.jhome.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private final ThreadLocal<Integer> stepCounter = ThreadLocal.withInitial(() -> 0);

    // Service: Step과 성공/실패 로그
    @Around("execution(* com.jhome.auth.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "_" + methodName;

        String steplog = "";
        int currentStep = stepCounter.get(); // 현재 Step 가져오기
        if(currentStep != 0){ // 컨트롤러에서 호출 된 서비스 메서드가 아니라면 step 출력하지 않음
            steplog = "[STEP "+ currentStep + "] ";
            stepCounter.set(currentStep + 1); // Step 증가
        }

        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("{}[{}] Error: {}", steplog, fullMethodName, e.getMessage());

            throw e;
        }

        long endTime = System.currentTimeMillis();
        log.info("{}[{}] Success, result = {} ,Execution Time: {}ms", steplog, fullMethodName, result, endTime - startTime);

        return result;
    }

}
