package com.express.activity.service.workflow_engine.custom_events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class ActivitiCustomEventRegistration<T> {

    Set<T> registry = new HashSet<>();

    public ActivitiCustomEventRegistration(Class<T> eventType, ApplicationContext applicationContext) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(eventType));
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("net.ccc.apps.core");

        Map<T, Integer> classesAndPriority = new HashMap<>();

        for (BeanDefinition bd : beanDefinitions) {
            Class<? extends T> classImpl = Class.forName(bd.getBeanClassName()).asSubclass(eventType);
            T customEvent = applicationContext.getBean(classImpl);
            int priorityValue;
            try {
                Field orderField = classImpl.getDeclaredField("priority");
                orderField.setAccessible(true);
                priorityValue = (int) orderField.get(customEvent);
            }
            catch (NoSuchFieldException e) { priorityValue = 0; }
            catch (IllegalAccessException e) { throw new RuntimeException(e); }
            classesAndPriority.put(customEvent, priorityValue);
        }

        List<Map.Entry<T, Integer>> nList = new ArrayList<>(classesAndPriority.entrySet());
        nList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        nList.forEach(el -> registry.add(el.getKey()));
    }
}
