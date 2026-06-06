package com.bike.aspect;

import com.bike.common.IpUtil;
import com.bike.common.OperationLog;
import com.bike.common.UserContext;
import com.bike.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Around("@annotation(com.bike.common.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        String module = annotation.module();
        String operationType = annotation.operationType();
        boolean sensitive = annotation.sensitive();

        Object[] args = joinPoint.getArgs();
        Object beforeData = null;
        Object afterData = null;

        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null && !arg.getClass().isPrimitive() && !arg.getClass().getName().startsWith("java.lang")) {
                    beforeData = arg;
                    break;
                }
            }
        }

        Object result = joinPoint.proceed();

        if (result != null && !result.getClass().isPrimitive() && !result.getClass().getName().startsWith("java.lang")) {
            afterData = result;
        }

        String operator = UserContext.getCurrentUser();
        String ipAddress = getIpAddress();

        String reason = null;
        if (sensitive) {
            reason = extractReason(args);
        }

        operationLogService.logOperation(operator, operationType, module, beforeData, afterData, ipAddress, reason);

        return result;
    }

    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return IpUtil.getIpAddr(request);
            }
        } catch (Exception e) {
            return "unknown";
        }
        return "unknown";
    }

    private String extractReason(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof String) {
                String str = (String) arg;
                if (str.length() > 2 && str.length() < 500) {
                    return str;
                }
            }
        }
        return null;
    }
}
