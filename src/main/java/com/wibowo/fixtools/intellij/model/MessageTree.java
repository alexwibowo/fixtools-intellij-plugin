package com.wibowo.fixtools.intellij.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;

public final class MessageTree {

    private final Map<String, Object> headerValues;

    private final Map<String, Object> bodyValues;

    private final DataDictionary dictionary;

    public MessageTree(final Message parsed,
                       final DataDictionary dictionary) throws FieldNotFound {
        final Message.Header header = parsed.getHeader();
        final Iterator<Field<?>> headerIterator = header.iterator();

        this.dictionary = dictionary;
        this.headerValues = new TreeMap<>();
        while (headerIterator.hasNext()) {
            final Field<?> next = headerIterator.next();
            final int tag = next.getTag();
            final String fieldName = dictionary.getFieldName(tag);
            headerValues.put(fieldName, next.getObject());
        }
        bodyValues = new TreeMap<>();
        bodyValues.putAll(simpleParse(parsed));
    }

    public Map<String, Object> getHeaderValues() {
        return headerValues;
    }

    public Map<String, Object> getBodyValues() {
        return bodyValues;
    }

    private Map<String, Object> simpleParse(final FieldMap object)
            throws FieldNotFound {
        final Map<String, Object> result = new TreeMap<>();
        final Iterator<Field<?>> bodyIterator = object.iterator();
        while (bodyIterator.hasNext()) {
            final Field<?> bodyField = bodyIterator.next();
            final String fieldName = dictionary.getFieldName(bodyField.getTag());
            if (fieldName != null) {
                result.put(fieldName, parseField(object, bodyField));
            } else {
                result.put("" + bodyField.getTag(), parseField(object, bodyField));
            }
        }
        return result;
    }

    private Object parseField(final FieldMap parent,
                              final Field<?> field) throws FieldNotFound {
        final int tag = field.getTag();
        final Object object = field.getObject();
        if (parent.hasGroup(tag)) {
            final List<Map<String, Object>> children = new ArrayList<>();
            // yep... 1 based index.
            for (int i = 1; i <= parent.getGroupCount(tag); i++) {
                final Group group = parent.getGroup(i, tag);
                children.add(simpleParse(group));
            }
            return children;
        } else {
            return object;
        }
    }

    public void clear() {
        headerValues.clear();
        bodyValues.clear();
    }
}
