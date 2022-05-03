package com.cobnet.spring.boot.dto;

import com.cobnet.common.Delegate;
import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.PageFieldType;
import com.cobnet.spring.boot.entity.Work;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class StaffRequiredServiceOption extends ObservationServiceOption<Integer> {

    private Map<Integer, Long> prices;

    public StaffRequiredServiceOption(@Nullable String description, Integer value, Map<Integer, Long> prices) {

        super("option.staff-required", description == null ? "Please choose how many staff you preferred to work with you!" : description, value);
    }

    public Map<Integer, Long> getPrices() {
        return prices;
    }

    @Override
    protected boolean onReceived(Work work, Integer value) {

        //TODO
        ((StaffRequiredServiceOption)work.getAttributes().get(this.name())).setValue(value);

        if(!prices.containsKey(value)) {

            return false;
        }

        long price = prices.get(value);

        work.setPrice(price);

        return true;
    }

    @Override
    public PageField[] getFields(Locale locale) {

        return new PageField[] { new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "1");
            delegator.put("value", 1);

            if(this.value() == 1) {

                delegator.put("checked", true);
            }

            return delegator;

        })), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "2");
            delegator.put("value", 2);

            if(this.value() == 2) {

                delegator.put("checked", true);
            }

            return delegator;

        })),new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            delegator.put("label", "3");

            if(this.value() == 3) {

                delegator.put("checked", true);
                delegator.put("value", 3);
            }

            return delegator;

        })), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

            try {

                delegator.put("label", ProjectBeanHolder.getTranslatorMessageSource().getMessage("other", locale));
                delegator.put("insertable", true);
                delegator.put("value", 4);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServiceDownException e) {
                throw new RuntimeException(e);
            }

            //TODO max check
            return delegator;

        }))};
    }
}
