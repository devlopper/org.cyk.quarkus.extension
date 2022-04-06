package org.cyk.quarkus.extension.core_.configuration;

import io.smallrye.config.WithDefault;

public interface Batch {
	@WithDefault("2000")
	Integer size();
}