package com.dimachine.core.locator;

import com.dimachine.core.annotation.ComponentScan;
import com.dimachine.core.annotation.ComponentScans;

import java.util.ArrayList;
import java.util.List;

public class ComponentPackageLocator {

    private final ComponentTraitsFactory componentTraitsFactory = new ComponentTraitsFactory();

    public ComponentTraits locate(List<? extends Class<?>> classesToScan) {
        OrComponentFilterCombiner combinedFilter = new OrComponentFilterCombiner();
        ComponentTraits allTraits = new ComponentTraits(new ArrayList<>(), combinedFilter);
        for (Class<?> configClass : classesToScan) {
            ComponentScan[] annotations = readComponentScanAnnotations(configClass);
            for (ComponentScan componentScan : annotations) {
                ComponentTraits newTrait = componentTraitsFactory.from(componentScan);
                allTraits.addTrait(newTrait);
                combinedFilter.combineWith(newTrait.getComponentFilter());
            }
        }
        return allTraits;
    }

    private ComponentScan[] readComponentScanAnnotations(Class<?> configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            return configClass.getAnnotationsByType(ComponentScan.class);
        } else if (configClass.isAnnotationPresent(ComponentScans.class)) {
            return configClass.getAnnotation(ComponentScans.class).value();
        }
        return new ComponentScan[0];
    }
}
