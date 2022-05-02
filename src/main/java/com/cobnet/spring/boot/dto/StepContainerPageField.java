package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.connection.web.PageFieldProperties;
import com.cobnet.spring.boot.dto.support.PageFieldType;

import java.util.Properties;

public class StepContainerPageField extends DynamicPageField {

    public StepContainerPageField(int groupId, String key, PageFieldType type, Properties properties, PageField... children) {
Delegate
        super(PageFieldType.CONTAINER, new Properties())
    }
}

public record StepContainerPageField(int groupId, String key, PageFieldType type, PageFieldProperties properties, PageField... children) implements PageField {

    public StepContainerPageField(int groupId, String key, String label, PageFieldType type, PageFieldProperties properties, PageField... children) {

        this(groupId, key, PageFieldType.CONTAINER, new DynamicPageFieldProperties(new KeyValuePair<>("isVisible", false)), new DynamicPageField[]{ new DynamicPageField(PageFieldType.LABEL, new DynamicPageFieldProperties(new KeyValuePair<>("value", label))), new DynamicPageField(type, properties, children) });
    }
}
