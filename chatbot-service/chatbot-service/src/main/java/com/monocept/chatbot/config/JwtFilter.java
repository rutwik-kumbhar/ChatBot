//package com.monocept.chatbot.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.util.Base64;
//import java.util.Collections;
//
//@Configuration
//public class JwtFilter  implements Filter {
//
//    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest  httpServletRequest = (HttpServletRequest)request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
//        String endPoint = httpServletRequest.getServletPath();
//        log.info("doFilter : api end point {} " , endPoint);
//
//        String header =  httpServletRequest.getHeader("Authorization");
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//            try {
//                boolean tokenValid = JwtUtil.isTokenValid(token);
//                log.info("token boolean value : {} ", tokenValid);
//                if(tokenValid){
//                    chain.doFilter(request, response);
//                }else {
//                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
//                }
//            } catch (Exception e) {
//                log.warn("JWT validation failed: {}", e.getMessage());
//                // You can optionally send an error here if you want to reject the request:
//                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
//            }
//        } else {
//            log.info("No Bearer token found in request");
//            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
//        }
//
//        chain.doFilter(request, response);
//    }
//}
//
