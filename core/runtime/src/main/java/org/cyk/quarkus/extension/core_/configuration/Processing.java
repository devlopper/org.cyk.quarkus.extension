package org.cyk.quarkus.extension.core_.configuration;

import io.smallrye.config.WithDefault;

public interface Processing {

	@WithDefault("true")
	Boolean sequential();
	
	Executor executor();
	
	Batch batch();
}