package com.express.activity.service.workflow_engine.custom_events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ActivitiCustomEventHandler<T> {

    private int priority = 0;

    public abstract void execute(T param);

}
