package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.connection.web.PageFieldProperties;
import com.cobnet.spring.boot.dto.support.PageFieldType;

public record StepContainerPageField(int groupId, String key, PageFieldType type, PageFieldProperties properties, PageField... children) implements PageField {

    public StepContainerPageField(int groupId, String key, String label, PageField... children) {

        this(groupId, key, PageFieldType.CONTAINER, )
    }
}
