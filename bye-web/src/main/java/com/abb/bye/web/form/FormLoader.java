package com.abb.bye.web.form;

import com.abb.bye.client.flow.Form;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
public class FormLoader {
    private static Map<String, Class<? extends Form>> mapping = new HashMap<>();

    static {
        Reflections reflections = new Reflections(FormLoader.class.getPackage().getName());
        Set<Class<? extends Form>> classes = reflections.getSubTypesOf(Form.class);
        classes.forEach(clazz -> mapping.put(clazz.getSimpleName(), clazz));
    }

    public static Form load(String name) throws IllegalAccessException, InstantiationException {
        Class<? extends Form> clazz = mapping.get(name);
        return clazz == null ? null : clazz.newInstance();
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        System.out.println(load("HolidayRequestForm"));
    }
}
