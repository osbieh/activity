package com.express.activity.service.workflow_engine.custom_events;

import lombok.Getter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Component
public class ActivitiCustomEventRegistry {

    private final ApplicationContext applicationContext;

    private Set<ActivitiCustomProcessCreatedEvent> customProcessCreatedEvents = new HashSet<>();
    private Set<ActivitiCustomProcessCompletedEvent> customProcessCompletedEvents = new HashSet<>();
    private Set<ActivitiCustomTaskCreatedEventHandler> customTaskCreatedEvents = new HashSet<>();
    private Set<ActivitiCustomTaskCompletedEvent> customTaskCompletedEvents = new HashSet<>();
    private Set<ActivitiCustomMailTaskEvent> customMailTaskEvents = new HashSet<>();

    @SuppressWarnings("unchecked")
    public ActivitiCustomEventRegistry(ApplicationContext applicationContext) throws ClassNotFoundException {
        this.applicationContext = applicationContext;
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ActivitiCustomEventHandler.class));
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("net.ccc.apps");
        Set<Class<?>> classes = beanDefinitions.stream().map(bd -> {
            Class<?> classObject = null;
            try { classObject = Class.forName(bd.getBeanClassName()); }
            catch (ClassNotFoundException e) { throw new RuntimeException(e); }
            return classObject.getSuperclass();
        }).collect(Collectors.toSet());

        for(Class<?> c: classes) {
            String[] classReference = c.getName().split("\\.");
            String className = classReference[classReference.length - 1];
            switch (className) {
                case "ActivitiCustomProcessCompletedEvent":
                    customProcessCompletedEvents = (Set<ActivitiCustomProcessCompletedEvent>) new ActivitiCustomEventRegistration<>(c, this.applicationContext).getRegistry();
                    break;
                case "ActivitiCustomTaskCreatedEvent":
                    customTaskCreatedEvents = (Set<ActivitiCustomTaskCreatedEventHandler>) new ActivitiCustomEventRegistration<>(c, this.applicationContext).getRegistry();
                    break;
                case "ActivitiCustomTaskCompletedEvent":
                    customTaskCompletedEvents = (Set<ActivitiCustomTaskCompletedEvent>) new ActivitiCustomEventRegistration<>(c, this.applicationContext).getRegistry();
                    break;
                case "ActivitiCustomProcessCreatedEvent":
                    customProcessCreatedEvents = (Set<ActivitiCustomProcessCreatedEvent>) new ActivitiCustomEventRegistration<>(c, this.applicationContext).getRegistry();
                    break;
                case "ActivitiCustomMailTaskEvent":
                    customMailTaskEvents = (Set<ActivitiCustomMailTaskEvent>) new ActivitiCustomEventRegistration<>(c, this.applicationContext).getRegistry();
                    break;
            }
        }
    }

}
