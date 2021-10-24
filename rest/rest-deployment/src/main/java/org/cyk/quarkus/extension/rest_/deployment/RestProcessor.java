package org.cyk.quarkus.extension.rest_.deployment;

import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.array.ArrayHelper;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class RestProcessor {

    private static final String FEATURE = "cyk rest";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
	UnremovableBeanBuildItem unremovablesNames() {
		return UnremovableBeanBuildItem.beanClassNames(UNREMOVALES_NAMES.toArray(new String[] {}));
	}
    
    private static final Collection<String> UNREMOVALES_NAMES = new ArrayList<>();
    static {
    	addUnremovable(UNREMOVALES_NAMES
    			,org.cyk.utility.rest.RequestProcessor.class
    			,org.cyk.utility.rest.ResponseBuilder.class
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