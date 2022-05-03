package com.cobnet.spring.boot.dto;

import com.cobnet.common.Delegate;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.dto.support.PageFieldType;

import java.util.Properties;

public class StepContainerPageField extends DynamicPageField {

    public StepContainerPageField(int groupId, String key, String label, PageFieldType type, Properties properties, PageField... children) {

        this(groupId, key, label, new DynamicPageField(type, properties, children));
    }

    public StepContainerPageField(int groupId, String key, String label, PageField... children) {

        super(PageFieldType.CONTAINER, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("groupId", groupId);
            delegator.put("key", key);
            delegator.put("isVisible", false);

            if(label != null) {

                delegator.put("label", label);
            }

            return delegator;

        }), children);
    }
}
