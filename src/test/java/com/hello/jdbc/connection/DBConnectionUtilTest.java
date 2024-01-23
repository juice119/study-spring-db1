package com.hello.jdbc.connection;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DBConnectionUtilTest {

	@Test
	void connection() {
		// given
		Connection connection = DBConnectionUtil.getConnection();

		// when & then
		assertThat(connection).isNotNull();
	}
}
