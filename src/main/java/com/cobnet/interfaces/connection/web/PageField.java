package com.cobnet.interfaces.connection.web;

import com.cobnet.spring.boot.dto.support.PageFieldType;

public interface PageField extends ApplicationJson {

    public PageFieldType type();

    public PageField[] children();

    public PageFieldProperties properties();
}
