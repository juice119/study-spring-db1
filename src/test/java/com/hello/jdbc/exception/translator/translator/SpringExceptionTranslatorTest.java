package com.hello.jdbc.exception.translator.translator;

import static com.hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringExceptionTranslatorTest {
	DataSource dataSource;

	@BeforeEach
	void init() {
		dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
	}

	@Test
	void sqlExceptionErrorCode() {
		String sql = "SELECT BAD GRAMMAR";

		try {
			Connection con = dataSource.getConnection();
			PreparedStatement statement = con.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			assertThat(e.getErrorCode()).isEqualTo(42122);
			int errorCode = e.getErrorCode();
			log.info("errorCode={}", errorCode);
			log.info("error", e);
		}
	}

	@Test
	void exceptionTranslator() {
		String sql = "SELECT BAD GRAMMAR";

		try {
			Connection con = dataSource.getConnection();
			PreparedStatement statement = con.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			assertThat(e.getErrorCode()).isEqualTo(42122);
			SQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(
				dataSource);
			DataAccessException resultEx = exTranslator.translate("SELECT123", sql, e);
			log.info("resultEx", resultEx);
			assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
		}
	}
}
