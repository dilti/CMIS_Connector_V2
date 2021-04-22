// Copyright (c) 2020 Boomi, Inc.
package com.lti.cmisconnector_v2;

import org.apache.http.entity.InputStreamEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RepeatableInputStreamEntity extends InputStreamEntity {

    public RepeatableInputStreamEntity(InputStream input, long length) {
        super(input, length);
        if (!input.markSupported()) {
            throw new IllegalArgumentException("input does not support mark/reset");
        }
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public InputStream getContent() throws IOException {
        InputStream content = super.getContent();
        content.reset();
        return content;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.getContent().reset();
        super.writeTo(outstream);
    }
}