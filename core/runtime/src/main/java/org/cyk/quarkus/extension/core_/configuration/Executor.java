package org.cyk.quarkus.extension.core_.configuration;

import java.util.concurrent.TimeUnit;

import io.smallrye.config.WithDefault;

public interface Executor {

	Timeout timeout();
	
	public interface Timeout {
		@WithDefault("5")
		Long duration();
		
		@WithDefault("MINUTES")
		TimeUnit unit();
	}
	
	Thread thread();
	
	public interface Thread {
		@WithDefault("4")
		Integer count();
	}
}