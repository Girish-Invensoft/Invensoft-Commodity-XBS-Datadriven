package hooks;

import base.DriverFactory;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import utils.Visual;

public class StepHudPlugin implements ConcurrentEventListener {
    private volatile String scenario = "";

    @Override
    public void setEventPublisher(EventPublisher p) {
        p.registerHandlerFor(TestCaseStarted.class, e -> scenario = e.getTestCase().getName());
        p.registerHandlerFor(TestStepStarted.class, e -> {
            if (e.getTestStep() instanceof PickleStepTestStep) {
                PickleStepTestStep s = (PickleStepTestStep) e.getTestStep();
                Visual.hud(DriverFactory.driver, scenario,
                        s.getStep().getKeyword() + s.getStep().getText());
            }
        });
    }
}