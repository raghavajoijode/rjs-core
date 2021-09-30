package com.subra.aem.rjs.examples.multicinfig.services.impl;

import com.subra.aem.rjs.examples.multicinfig.services.MyTestSampleService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MyTestSampleService.class)
@Designate(ocd = MyTestSampleServiceImpl.Config.class)
public class MyTestSampleServiceImpl implements MyTestSampleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTestSampleServiceImpl.class);

    private String memberName;
    private String memberPlace;
    private int pinCode;

    @Activate
    protected void activate(final Config config) {
        LOGGER.debug("Inside {}", this.getClass());
        memberName = config.getMemberName();
        memberPlace = config.getMemberPlace();
        pinCode = config.getMemberPIN();
    }

    @Override
    public String getName() {
        return memberName;
    }

    @Override
    public String getPlace() {
        return memberPlace;
    }

    @Override
    public int getPIN() {
        return pinCode;
    }

    @ObjectClassDefinition(name = "MyTestSampleService configuration")
    public @interface Config {

        @AttributeDefinition(name = "Name", defaultValue = "Raghava")
        String getMemberName();

        @AttributeDefinition(name = "Place", defaultValue = "Hyderabad")
        String getMemberPlace();

        @AttributeDefinition(name = "Pin", defaultValue = "500001")
        int getMemberPIN();
    }

}
