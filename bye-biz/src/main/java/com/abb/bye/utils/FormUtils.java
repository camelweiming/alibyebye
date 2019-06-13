package com.abb.bye.utils;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
public class FormUtils {

    //public static void setFieldsFromVariables(Form object, Map<String, Object> variables) throws IllegalAccessException {
    //    Class<?> c = object.getClass();
    //    if (variables == null) {
    //        return;
    //    }
    //    for (Field field : c.getDeclaredFields()) {
    //        com.abb.bye.client.flow.Field f = field.getAnnotation(com.abb.bye.client.flow.Field.class);
    //        if (f == null) {
    //            continue;
    //        }
    //        field.setAccessible(true);
    //        Object value = variables.get(field.getName());
    //        if (value == null) {
    //            continue;
    //        }
    //        if (field.getType() == value.getClass()) {
    //            field.set(object, value);
    //        }
    //    }
    //}
    //
    //public static List<FormField> getFields(Form object) throws IllegalAccessException {
    //    return getFields(object, null);
    //}
    //
    //public static List<FormField> getFieldsOnlyPersistent(Form object) throws IllegalAccessException {
    //    return getFields(object, field -> field.persistence());
    //}
    //
    //public static List<FormField> getFields(Form object, Predicate<com.abb.bye.client.flow.Field> filter) throws IllegalAccessException {
    //    List<FormField> formFields = new ArrayList<>();
    //    Class<?> c = object.getClass();
    //    for (Field field : c.getDeclaredFields()) {
    //        com.abb.bye.client.flow.Field f = field.getAnnotation(com.abb.bye.client.flow.Field.class);
    //        if (f == null) {
    //            continue;
    //        }
    //        if (filter != null && !filter.test(f)) {
    //            continue;
    //        }
    //        field.setAccessible(true);
    //        FormField formField = new FormField();
    //        formField.setLabel(f.label());
    //        formField.setName(f.name());
    //        formField.setRequired(f.required());
    //        formField.setType(f.type());
    //        Object v = field.get(object);
    //        if (f.multiValue()) {
    //            formField.setOptions((List<FormFieldOption>)v);
    //        } else {
    //            formField.setValue(v == null ? null : v.toString());
    //        }
    //        formFields.add(formField);
    //    }
    //    return formFields;
    //}
}
