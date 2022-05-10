package org.cyk.quarkus.extension.core_.configuration.processing;

import org.cyk.quarkus.extension.core_.configuration.Configuration;
import org.cyk.quarkus.extension.core_.configuration.Executor;
import org.cyk.quarkus.extension.core_.configuration.Configuration.Batch;

import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;

public interface Processing {

	@WithDefault("0 */30 * ? * *")
	@WithConverter(Configuration.StringConverter.class)
	String cron();
	
	@WithDefault("true")
	Boolean sequential();
	
	Executor executor();
	
	Batch batch();
}