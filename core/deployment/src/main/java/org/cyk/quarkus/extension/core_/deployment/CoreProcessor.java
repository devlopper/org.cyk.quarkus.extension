package org.cyk.quarkus.extension.core_.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.cyk.utility.__kernel__.array.ArrayHelper;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class CoreProcessor {

    private static final String FEATURE = "cyk core";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
	UnremovableBeanBuildItem unremovables() {
		return UnremovableBeanBuildItem.beanClassNames(UNREMOVALES.stream().map(Class::getName).collect(Collectors.toSet()));
	}
    
    @BuildStep
	UnremovableBeanBuildItem unremovablesNames() {
		return UnremovableBeanBuildItem.beanClassNames(UNREMOVALES_NAMES.toArray(new String[] {}));
	}
    
    private static final Collection<Class<?>> UNREMOVALES = new ArrayList<>();
    private static final Collection<String> UNREMOVALES_NAMES = new ArrayList<>();
    static {
    	UNREMOVALES.addAll(List.of(
    			org.cyk.utility.__kernel__.configuration.ValueCheckerImpl.class
    			));
    	addUnremovable(UNREMOVALES_NAMES
    			,org.cyk.utility.__kernel__.collection.CollectionProcessor.class
    			,org.cyk.utility.__kernel__.value.ValueConverter.class
    			,org.cyk.utility.__kernel__.runnable.Runner.class
    			,org.cyk.utility.__kernel__.mapping.MapperGetter.class
    			,org.cyk.utility.__kernel__.mapping.MapperClassGetter.class
    			,org.cyk.utility.__kernel__.mapping.MapperClassNameGetter.class
    			,org.cyk.utility.__kernel__.mapping.MappingSourceBuilder.class
    			,org.cyk.utility.__kernel__.value.Checker.class
    			,org.cyk.utility.__kernel__.protocol.http.HttpClientGetter.class
    			,org.cyk.utility.__kernel__.locale.LocaleGetter.class
    			);
    }
    
    private static void addUnremovable(Class<?> interfaceClass,Collection<String> unremovablesNames) {
    	unremovablesNames.add(interfaceClass.getName());
    	unremovablesNames.add(interfaceClass.getName()+"Impl");
    }
    
    private static void addUnremovable(Collection<String> unremovablesNames,Class<?>...interfaceClass) {
    	if(ArrayHelper.isEmpty(interfaceClass))
    		return;
    	for(Class<?> klass : interfaceClass)
    		addUnremovable(klass, unremovablesNames);
    }
}
