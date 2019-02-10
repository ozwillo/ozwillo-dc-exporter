package org.ozwillo.dcexporter.util;

import org.oasis_eu.spring.datacore.model.DCResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DCResourceUtils {

    public static String getI18nFieldValueFromList(List<DCResource.Value> valueList, String lang) {
        // Trying to handle structures like that :
        // resource:name=[{@value=Test Ozwillo, @language=en}, {@value=Test Ozwillo, @language=fr}, ...]
        // which are lists of two entries maps, second entry having the language for value !

        Optional<DCResource.Value> result = valueList.stream().filter(value ->
                value.asMap().values().toArray()[1].toString().equals(lang)
        ).findFirst();

        return result.isPresent() ? result.get().asMap().values().toArray()[0].toString() : "";
    }

    public static String getI18nFieldValue(Map<String, String> field, String lang) {
        Optional<Map.Entry<String, String>> value = field.entrySet().stream()
                .filter(entry -> lang.equals(entry.getValue())).findFirst();

        return value.isPresent() ? value.get().getKey() : "";
    }

}
