package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.Page;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.connection.web.PageProperties;

public record DynamicPage(PageProperties properties, PageField... fields) implements Page {

}
