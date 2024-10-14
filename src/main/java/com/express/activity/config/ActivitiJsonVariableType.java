package com.express.activity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;

public class ActivitiJsonVariableType implements VariableType {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActivitiJsonVariableType() {
        objectMapper.findAndRegisterModules();
    }

    @Override
    public String getTypeName() {
        return "json";
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public boolean isAbleToStore(Object value) {
        return true;
    }

    @Override
    public void setValue(Object value, ValueFields valueFields) {
        try {
            String json = objectMapper.writeValueAsString(value);
            valueFields.setTextValue(json);
            if(value != null)
                valueFields.setTextValue2(value.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue(ValueFields valueFields) {
        try {
            if (valueFields.getTextValue() != null && !valueFields.getTextValue().isEmpty() && !valueFields.getTextValue2().isEmpty()) {
                Class<?> clazz = Class.forName(valueFields.getTextValue2());
                String json = valueFields.getTextValue();
                return objectMapper.readValue(json, clazz);
//                return objectMapper.readValue(json, new TypeReference<>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
