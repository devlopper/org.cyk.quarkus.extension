package org.cyk.quarkus.extension.persistence.hibernate.orm.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.cyk.utility.__kernel__.array.ArrayHelper;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class PersistenceHibernateOrmProcessor {

    private static final String FEATURE = "cyk-persistence-hibernate-orm";

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
    
    @BuildStep
	AdditionalBeanBuildItem additionalBeanApplicationLifeCycleListener() {
		AdditionalBeanBuildItem item = AdditionalBeanBuildItem.unremovableOf(org.cyk.quarkus.extension.hibernate.orm.ApplicationLifeCycleListener.class);
		return item;
	}
    
    @BuildStep
	AdditionalBeanBuildItem additionalBeanQueryExecutorArgumentsDto() {
		AdditionalBeanBuildItem item = AdditionalBeanBuildItem.unremovableOf(org.cyk.utility.persistence.query.QueryExecutorArguments.Dto.class);
		return item;
	}
    
    @BuildStep
	AdditionalBeanBuildItem additionalBeanQueryExecutorArgumentsDtoMapper() {
		AdditionalBeanBuildItem item = AdditionalBeanBuildItem.unremovableOf(org.cyk.utility.persistence.query.QueryExecutorArguments.Dto.Mapper.class);
		return item;
	}
    
    private static final Collection<Class<?>> UNREMOVALES = new ArrayList<>();
    private static final Collection<String> UNREMOVALES_NAMES = new ArrayList<>();
    static {
    	UNREMOVALES.addAll(List.of(
    			org.cyk.utility.persistence.server.QueryManagerImpl.class			
    			,org.cyk.utility.persistence.query.QueryExecutor.class
    			,org.cyk.utility.persistence.server.query.QueryExecutorImpl.class
    			,org.cyk.utility.persistence.query.QueryExecutorArguments.Dto.class
    			,org.cyk.utility.persistence.query.QueryExecutorArguments.Dto.Mapper.class
    			
    			,org.cyk.utility.persistence.query.Filter.Dto.class
    			,org.cyk.utility.persistence.query.Filter.Dto.Mapper.class
    			
    			,org.cyk.utility.persistence.server.procedure.ProcedureExecutorGetterImpl.class
    			,org.cyk.utility.persistence.server.hibernate.ProcedureExecutorImpl.class
    			,org.cyk.utility.persistence.server.hibernate.MaterializedViewManagerImpl.class
    			
    			,org.cyk.utility.persistence.server.SpecificPersistenceGetter.class
    			));
    	addUnremovable(UNREMOVALES_NAMES
    			,org.cyk.utility.__kernel__.object.__static__.persistence.EntityLifeCycleListener.class
    			,org.cyk.utility.persistence.entity.EntityLifeCycleListener.class
    			,org.cyk.utility.persistence.server.hibernate.Initializer.class
    			,org.cyk.utility.persistence.query.QueryManager.class
    			,org.cyk.utility.persistence.query.QueryIdentifierBuilder.class
    			,org.cyk.utility.persistence.query.QueryIdentifierGetter.class
    			,org.cyk.utility.persistence.query.QueryGetter.class
    			,org.cyk.utility.persistence.query.QueryValueBuilder.class
    			,org.cyk.utility.persistence.query.QueryResultMapper.class
    			,org.cyk.utility.persistence.query.QueryResultProcessor.class
    			,org.cyk.utility.persistence.query.CountQueryIdentifierGetter.class
    			//,org.cyk.utility.persistence.query.QueryExecutor.class
    			
    			,org.cyk.utility.persistence.query.EntityFinder.class
    			
    			,org.cyk.utility.persistence.server.TransientFieldsProcessor.class
    			,org.cyk.utility.persistence.server.query.executor.DynamicManyExecutor.class
    			,org.cyk.utility.persistence.server.query.executor.DynamicOneExecutor.class
    			,org.cyk.utility.persistence.server.query.executor.field.GenericFieldExecutor.class
    			,org.cyk.utility.persistence.server.query.executor.field.CodeExecutor.class
    			,org.cyk.utility.persistence.server.query.executor.field.IdentifierExecutor.class
    			,org.cyk.utility.persistence.server.query.RuntimeQueryBuilder.class
    			,org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.CaseStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.EqualStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.FromStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.GroupStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.JoinStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.LikeStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.LikeStringValueBuilder.class
    			,org.cyk.utility.persistence.server.query.string.OrderStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.SelectStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.QueryStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.WhereStringBuilder.class
    			,org.cyk.utility.persistence.server.query.string.HavingStringBuilder.class
    			,org.cyk.utility.persistence.EntityManagerFactoryGetter.class
    			,org.cyk.utility.persistence.EntityManagerGetter.class
    			);
    	UNREMOVALES_NAMES.add("org.cyk.utility.persistence.query.QueryExecutorArguments$Dto$MapperImpl");
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