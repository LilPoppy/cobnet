package com.cobnet.spring.boot.dto;

import com.cobnet.common.Delegate;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.dto.support.PageFieldType;
import com.cobnet.spring.boot.entity.Work;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;
import java.util.Properties;

public class StaffRequiredServiceOption extends ObservationServiceOption<Integer> {

    public StaffRequiredServiceOption(@Nullable String description, Integer value) {

        super("staff-required", description == null ? "Please choose how many staff you preferred to work with you!" : description, value);
    }

    @Override
    protected boolean onReceived(Work work, Integer value) {

        //TODO
        ((StaffRequiredServiceOption)work.getAttributes().get(this.name())).setValue(value);

        return true;
    }

    @Override
    public PageField[] getFields(Locale locale) {

        return new PageField[] { new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "1");

            if(this.value() == 1) {

                delegator.put("checked", true);
            }

            return delegator;

        })), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "2");

            if(this.value() == 2) {

                delegator.put("checked", true);
            }

            return delegator;

        })),new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "3");

            if(this.value() == 3) {

                delegator.put("checked", true);
            }

            return delegator;

        })), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "other");

            //TODO max check
            return delegator;

        }))};
    }
}
