package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.interfaces.spring.dto.ServiceOptionGenerator;
import com.cobnet.interfaces.spring.dto.annotation.ServiceOptionDefined;
import org.springframework.stereotype.Component;

public class StaffRequireServiceOption extends ServiceOptionBase<Integer> {

    public StaffRequireServiceOption(String name, Class<Integer> type) {
        super(name, type);
    }

    @Override
    public ServiceOptionGenerator<? extends ServiceOption<Integer>, Integer> getGenerator() {

        return new StaffRequireServiceOptionGenerator();
    }

    @Component
    public static class StaffRequireServiceOptionGenerator implements ServiceOptionGenerator<StaffRequireServiceOption, Integer> {

        @ServiceOptionDefined(names = "StaffRequire")
        @Override
        public StaffRequireServiceOption generate(String name) {

            return new StaffRequireServiceOption(name, Integer.class);
        }
    }
}
