package com.hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
	private final DataSource dataSource;
	private final MemberRepositoryV2 repository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		Connection connection = dataSource.getConnection();

		try {
			connection.setAutoCommit(false);
			biz(connection, fromId, toId, money);
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			throw new IllegalStateException(e);
		} finally {
			release(connection);
		}
	}

	private void release(Connection con) {
		if (con != null) {
			try {
				con.setAutoCommit(true);
				con.close();
			} catch (SQLException e) {
				log.info("error", e);
			}
		}
	}

	private void biz(Connection con, String fromId, String toId, int money) throws SQLException {
		Member fromMember = repository.findById(con, fromId);
		Member toMember = repository.findById(con, toId);

		repository.update(con, fromId, fromMember.getMoney() - money);
		validation(toMember);
		repository.update(con, toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}
}
