package com.twoweekhee.booksearch.support;

import org.testcontainers.containers.PostgreSQLContainer;

public final class PostgreSQLTestContainer {

	private static volatile PostgreSQLContainer<?> container;

	public static PostgreSQLContainer<?> getContainer() {
		if (container == null) {
			synchronized (PostgreSQLTestContainer.class) {
				if (container == null) {
					container = new PostgreSQLContainer<>("postgres:16")
						.withDatabaseName("testdb")
						.withUsername("test")
						.withPassword("test")
						.withInitScript("db/schema.sql")
						.withReuse(true);

					container.start();

					Runtime.getRuntime().addShutdownHook(new Thread(container::stop));
				}
			}
		}
		return container;
	}
}
