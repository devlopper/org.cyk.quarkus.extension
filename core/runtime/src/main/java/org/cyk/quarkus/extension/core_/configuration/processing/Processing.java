package org.cyk.quarkus.extension.core_.configuration.processing;

import org.cyk.quarkus.extension.core_.configuration.Batch;
import org.cyk.quarkus.extension.core_.configuration.Executor;

import io.smallrye.config.WithDefault;

public interface Processing {

	@WithDefault("true")
	Boolean sequential();
	
	Executor executor();
	
	Batch batch();
}