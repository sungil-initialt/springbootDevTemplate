package com.sptek.webfw.support;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
Controller 이전단계(필터등)에서 request의 ioStream이 읽어진 경우 이를 대체하기 위한 대체 request 용도임
(한번 읽어진 request는 controller 에서 다시 읽을수 없음)
 */
public class HttpServletRequestWrapperSupport extends HttpServletRequestWrapper {

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletInputStream;

    public HttpServletRequestWrapperSupport(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletInputStream = new ResettableServletInputStream();
    }

    public void resetInputStream(byte[] data) {
        servletInputStream.inputStream = new ByteArrayInputStream(data);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletInputStream.inputStream = new ByteArrayInputStream(rawData);
        }
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader(), StandardCharsets.UTF_8);
            servletInputStream.inputStream = new ByteArrayInputStream(rawData);
        }
        return new BufferedReader(new InputStreamReader(servletInputStream));
    }


    private class ResettableServletInputStream extends ServletInputStream {
        private InputStream inputStream;

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}
