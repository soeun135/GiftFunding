package com.soeun.GiftFunding.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.ErrorResponse;
import com.soeun.GiftFunding.type.ErrorType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("인가 실패");

        sendErrorResponse(response, "인증 실패");
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        TokenException e = new TokenException(ErrorType.INVALID_TOKEN);
        log.info("CustomAccessDenied");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(e.getErrorCode().getDescription()));
    }
}
