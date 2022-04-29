package org.cyk.quarkus.extension.core_;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.PropertiesConfigSource;

public class ConfigSourceFactory implements io.smallrye.config.ConfigSourceFactory,Serializable {

	protected Boolean isRuntimePhase() {
		try {
			classForName("io.quarkus.deployment.steps.RuntimeConfigSetup");
			return Boolean.FALSE;
		} catch (ClassNotFoundException exception) {
			return Boolean.TRUE;
		}
	}
	
	protected void classForName(String name) throws ClassNotFoundException {
		Class.forName(name);
	}

	private Boolean isReadableFromDataSource(ConfigSourceContext configSourceContext) {
		return "true".equals(read(configSourceContext, "cyk.configuration.readable-from-datasource"));
	}
	
	@Override
	public Iterable<ConfigSource> getConfigSources(ConfigSourceContext configSourceContext) {
		if(Boolean.TRUE.equals(isRuntimePhase()))
			return Collections.emptyList();
		ArrayList<ConfigSource> collection = new ArrayList<>();
		if(Boolean.TRUE.equals(isReadableFromDataSource(configSourceContext))) {
			LogHelper.logInfo("Configuration will be read from data source", getClass());
			ConfigSource configSource = buildFromDataSource(configSourceContext);
			if(configSource == null)
				LogHelper.logInfo("Configuration cannot be read from data source", getClass());
			else
				collection.add(configSource);
		}
		return collection;
	}

	private ConfigSource buildFromDataSource(ConfigSourceContext configSourceContext) {
		String query = getJdbcQuery(configSourceContext);
		if(StringHelper.isBlank(query)) {
			LogHelper.logSevere("Query cannot be derived.", getClass());
			return null;
		}
		
		String databaseGeneration = read(configSourceContext, "quarkus.hibernate-orm.database.generation");
		if("create".equals(databaseGeneration) || "drop".equals(databaseGeneration) || "drop-and-create".equals(databaseGeneration)) {
			LogHelper.logSevere(String.format("Data generation mode is %s",databaseGeneration), getClass());
			return null;
		}
		
		if(!Boolean.TRUE.equals(loadJdbcDriver(getJdbcDriverClassName(configSourceContext)))) {
			LogHelper.logSevere("JDBC driver cannot be loaded", getClass());
			return null;
		}
		try {
			try (Connection connection = getJdbcConnection(configSourceContext)) {
				if(connection == null)
					return null;
				PreparedStatement preparedStatement;
				try {
					preparedStatement = connection.prepareStatement(query);
				} catch (Exception exception) {
					LogHelper.logSevere(String.format("JDBC statement cannot be built : %s",exception.getMessage()), getClass());
					return null;
				}
	            ResultSet resultSet;
				try {
					resultSet = preparedStatement.executeQuery();
				} catch (Exception exception) {
					LogHelper.logSevere(String.format("JDBC query cannot be executed : %s",exception.getMessage()), getClass());
					return null;
				}
	            Map<String,String> map = new HashMap<>();
	            String columnCode = getColumnCode(configSourceContext);
	            String columnValue = getColumnValue(configSourceContext);
	            while (resultSet.next())
	                map.put(resultSet.getString(columnCode), resultSet.getString(columnValue));
	            resultSet.close();
	            preparedStatement.close();
	            connection.close();
	            LogHelper.logInfo(String.format("Configuration from data source : %s", map), getClass());
				return new PropertiesConfigSource(map, getJdbcName(configSourceContext), 900);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	private String getJdbcQuery(ConfigSourceContext configSourceContext) {
		String query = read(configSourceContext, "cyk.configuration.query");
		if(StringHelper.isBlank(query)) {
			String table = ValueHelper.defaultToIfBlank(read(configSourceContext, "cyk.configuration.table"),"TA_PROPRIETE_CONFIGURATION");
			if(StringHelper.isBlank(table))
				return null;
			String columnCode = getColumnCode(configSourceContext);
			if(StringHelper.isBlank(columnCode))
				return null;
			String columnValue = getColumnValue(configSourceContext);
			if(StringHelper.isBlank(columnValue))
				return null;
			query = String.format("SELECT %s,%s FROM %s", columnCode,columnValue,table);
		}
		return query;
	}
	
	private String getColumnCode(ConfigSourceContext configSourceContext) {
		return ValueHelper.defaultToIfBlank(read(configSourceContext, "cyk.configuration.column.code"),"code");
	}
	
	private String getColumnValue(ConfigSourceContext configSourceContext) {
		return ValueHelper.defaultToIfBlank(read(configSourceContext, "cyk.configuration.column.value"),"valeur");
	}
	
	private String getJdbcName(ConfigSourceContext configSourceContext) {
		return ValueHelper.defaultToIfBlank(read(configSourceContext, "cyk.configuration.name"),"ConfigurationFromDataBase");
	}
	
	private Connection getJdbcConnection(ConfigSourceContext configSourceContext) {
		String url = read(configSourceContext, "quarkus.datasource.jdbc.url",Boolean.TRUE);
		if(StringHelper.isBlank(url))
			return null;
		String username = read(configSourceContext, "quarkus.datasource.username",Boolean.TRUE);
		if(StringHelper.isBlank(username))
			return null;
		String password = read(configSourceContext, "quarkus.datasource.password",Boolean.TRUE);
		if(StringHelper.isBlank(password))
			return null;
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException exception) {
			LogHelper.logWarning(String.format("Data source jdbc connection cannot be acquired : %s",exception.getMessage()), getClass());
			return null;
		}
	}
	
	private Boolean loadJdbcDriver(String className) {
		if(StringHelper.isBlank(className))
			return null;
		try {
			Class.forName(className);
			return Boolean.TRUE;
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	private String getJdbcDriverClassName(ConfigSourceContext configSourceContext) {
		String name = read(configSourceContext,"quarkus.datasource.jdbc.driver");
		if(StringHelper.isNotBlank(name))
			return name;
		name = read(configSourceContext,"quarkus.datasource.db-kind",Boolean.TRUE);
		if(StringHelper.isBlank(name))
			return null;
		switch(name) {
		case "h2" : return "org.h2.Driver";
		case "oracle" : return "oracle.jdbc.driver.OracleDriver";
		case "mysql" : return "com.mysql.cj.jdbc.Driver";
		default: return null;
		}
	}
	
	private String read(ConfigSourceContext configSourceContext,String name,Boolean loggableIfNull) {
		if(StringHelper.isBlank(name))
			return null;
		ConfigValue configValue;
		try {
			configValue = configSourceContext.getValue(name);
		} catch (Exception exception) {
			LogHelper.logWarning(String.format("Exception while reading %s : %s",name,exception.getMessage()), getClass());
			return null;
		}
		if (configValue == null || configValue.getValue() == null) {
			if(Boolean.TRUE.equals(loggableIfNull))
				LogHelper.logWarning(String.format("Configuration property <<%s>> not found", name), getClass());
			return null;
		}
		return configValue.getValue();
	}
	
	private String read(ConfigSourceContext configSourceContext,String name) {
		return read(configSourceContext, name, Boolean.FALSE);
	}
}