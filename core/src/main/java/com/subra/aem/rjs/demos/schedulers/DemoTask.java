package com.subra.aem.rjs.demos.schedulers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = DemoTask.Config.class)
@Component(service = DemoTask.class, enabled = false)
public class DemoTask implements Runnable {

    @ObjectClassDefinition(name = "A Demo scheduled task")
    @interface Config {
        @AttributeDefinition(name = "A parameter", description = "sample value")
        String myParameter() default "DemoJob-value";
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String myParameter;

    @Override
    public void run() {
        logger.info("\n\n________________Will do some task when scheduled.... {}\n\n", myParameter);
    }

    @Activate
    protected void activate(final Config config) {
        logger.info("************************************** ACTIVATED ?????? {} ????? ******************************************", config.myParameter());
        myParameter = config.myParameter();
    }

}
