package com.hello.jdbc.basic;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnCheckedAppTest {
	@Test
	void checked() {
		Controller controller = new Controller();
		assertThatThrownBy(controller::request).isInstanceOf(Exception.class);
	}

	static class Controller {

		private final Service service = new Service();

		public void request() {
			service.logic();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();

		public void logic() {
			repository.call();
			networkClient.call();
		}
	}

	static class Repository {

		public void call() {
			try {
				runSQL();
			} catch (SQLException e) {
				throw new RuntimeSQLException(e);
			}
		}

		private void runSQL() throws SQLException {
			throw new SQLException("ex");
		}
	}

	static class NetworkClient {
		public void call() {
			throw new RuntimeConnectException("연결 실패");
		}
	}

	static class RuntimeSQLException extends RuntimeException {
		public RuntimeSQLException(Throwable cause) {
			super(cause);
		}
	}

	static class RuntimeConnectException extends RuntimeException {
		public RuntimeConnectException(String message) {
			super(message);
		}
	}

}
