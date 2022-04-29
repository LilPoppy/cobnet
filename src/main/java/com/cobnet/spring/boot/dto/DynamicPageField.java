package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.connection.web.PageFieldProperties;
import com.cobnet.spring.boot.dto.support.PageFieldType;

public record DynamicPageField(PageFieldType type, PageFieldProperties properties, PageField... children) implements PageField {

    
}
