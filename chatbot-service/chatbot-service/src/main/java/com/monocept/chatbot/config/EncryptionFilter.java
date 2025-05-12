//package com.monocept.chatbot.config;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.monocept.chatbot.exceptions.RequestDecryptionException;
//import com.monocept.chatbot.exceptions.RequestEncryptionException;
//import com.monocept.chatbot.utils.RequestDecryptionUtil;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletResponseWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StreamUtils;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class EncryptionFilter  implements Filter {
//
//
//   private final RequestDecryptionUtil requestDecryptionUtil;
//   private  final  ObjectMapper objectMapper;
//
//    public EncryptionFilter(RequestDecryptionUtil requestDecryptionUtil, ObjectMapper objectMapper) {
//        this.requestDecryptionUtil = requestDecryptionUtil;
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        log.info("doFilter : encryption start");
//        byte[] inputStreamBytes = StreamUtils.copyToByteArray(request.getInputStream());
//        HttpServletRequest  httpServletRequest = (HttpServletRequest)request;
//
//        String endPoint = httpServletRequest.getServletPath();
//        log.info("doFilter : api end point {} " , endPoint);
//
//        httpServletRequest.getHeader("elychat");
//
//        if (inputStreamBytes.length > 0) {
//            Map<String, String> jsonRequest = objectMapper.readValue(inputStreamBytes, Map.class);
//            String payload = jsonRequest.get("payload");
//            log.info("doFilter : - encryption request  payload: {}", payload);
//
//            String decryptedPayload = requestDecryptionUtil.decrypt(payload);
//            log.info("doFilter :  decrypted request payload : {}", decryptedPayload);
//
//            try {
//                JsonNode jsonNode = objectMapper.readTree(decryptedPayload);
//                log.info("doFilter : jsonNode : {}", jsonNode);
//                String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
//                log.info("doFilter : prettyJsonString : {}", prettyJsonString);
//                HttpServletRequest newRequest = new DecryptedPayloadRequestWrapper((HttpServletRequest) request, prettyJsonString);
//
//                HttpServletResponseWrapper responseWrapper = new EncryptedPayloadResponseWrapper((HttpServletResponse) response);
//
//                chain.doFilter(newRequest, responseWrapper);
//
//                byte[] encryptedResponseBytes = ((EncryptedPayloadResponseWrapper) responseWrapper).getCapturedResponse();
//
//                String responsePayload = new String(encryptedResponseBytes, StandardCharsets.UTF_8);
//
//                String encryptedResponsePayload = requestDecryptionUtil.encrypt(responsePayload);
//
//                String jsonResponse = "{\"payload\":\"" + encryptedResponsePayload + "\"}";
//                response.getWriter().write(jsonResponse);
//            }catch (RequestEncryptionException | RequestDecryptionException e){
//                throw new RuntimeException(e.getMessage());
//            } catch (Exception e){
//                log.info("doFilter : exception : {}" , e.getMessage());
//                throw  new RuntimeException(e.getMessage());
//            }
//
//
//        }
//
//    }
//
//    private static class DecryptedPayloadRequestWrapper extends HttpServletRequestWrapper {
//
//        private final String decryptedPayload;
//
//        public DecryptedPayloadRequestWrapper(HttpServletRequest request, String decryptedPayload) {
//            super(request);
//            this.decryptedPayload = decryptedPayload;
//        }
//
//        @Override
//        public ServletInputStream getInputStream() throws IOException {
//            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedPayload.getBytes(StandardCharsets.UTF_8));
//
//            return new ServletInputStream() {
//                @Override
//                public boolean isFinished() {
//                    return false;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return false;
//                }
//
//                @Override
//                public void setReadListener(ReadListener listener) {
//                    // Not Required but can not be removed as we had to override all the functions
//                }
//
//                @Override
//                public int read() throws IOException {
//                    return byteArrayInputStream.read();
//                }
//            };
//        }
//    }
//
//    private static class EncryptedPayloadResponseWrapper extends HttpServletResponseWrapper {
//
//        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        private final PrintWriter writer = new PrintWriter(byteArrayOutputStream);
//
//        public EncryptedPayloadResponseWrapper(HttpServletResponse response) {
//            super(response);
//        }
//
//        @Override
//        public PrintWriter getWriter() throws IOException {
//            return writer;
//        }
//
//        @Override
//        public ServletOutputStream getOutputStream() throws IOException {
//            return new ServletOutputStream() {
//                @Override
//                public boolean isReady() {
//                    return false;
//                }
//
//                @Override
//                public void setWriteListener(WriteListener listener) {
//                    // Not Required but can not be removed as we had to override all the functions
//                }
//
//                @Override
//                public void write(int b) throws IOException {
//                    byteArrayOutputStream.write(b);
//                }
//            };
//        }
//
//        public byte[] getCapturedResponse() {
//            return byteArrayOutputStream.toByteArray();
//        }
//    }
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
