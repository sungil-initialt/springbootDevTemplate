package com.sptek._frameworkWebCore.support.deplicated;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


public class DEPRECATED_HttpServletResponseWrapperSupport extends HttpServletResponseWrapper {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(outputStream, true);

    public DEPRECATED_HttpServletResponseWrapperSupport(HttpServletResponse response) {
        super(response);
    }

    //body를 읽어도 소멸되지 않는다.
    public String getResponseBody() {
        writer.flush();
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    //새로운 body로 저장된다.
    public void setResponseBody(String body) throws IOException {
        outputStream.reset();
        writer.write(body);
        writer.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new DEPRECATED_CustomServletOutputStream(outputStream);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}

class DEPRECATED_CustomServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream buffer;

    public DEPRECATED_CustomServletOutputStream(ByteArrayOutputStream buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    @Override
    public boolean isReady() {
        return true; // 항상 준비된 상태 반환
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        // 동기 방식이므로 바로 처리 완료 알림
        if (listener != null) {
            try {
                listener.onWritePossible();
            } catch (IOException e) {
                throw new RuntimeException("WriteListener 처리 중 에러 발생", e);
            }
        }
    }
}