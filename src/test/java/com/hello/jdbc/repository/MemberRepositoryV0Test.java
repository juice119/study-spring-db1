package com.hello.jdbc.repository;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hello.jdbc.connection.DBConnectionUtil;
import com.hello.jdbc.domain.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MemberRepositoryV0Test {
	private final MemberRepositoryV0 repository = new MemberRepositoryV0();

	@BeforeEach()
	void cleatDB() throws SQLException {
		Connection connection = DBConnectionUtil.getConnection();
		PreparedStatement psmt = connection.prepareStatement("DELETE FROM member");

		psmt.executeUpdate();

		psmt.close();
		connection.close();
	}

	@Test
	void save() throws SQLException {
		// given
		String memberId = "memberV0";
		Member member = new Member(memberId, 10_000);

		// when
		repository.save(member);

		// then
		Member findeMember = repository.findById(memberId);
		assertThat(findeMember).isEqualTo(findeMember);
	}

	@Test
	void update() throws SQLException {
		// given
		String memberId = "memberV0";
		Member member = new Member(memberId, 10_000);
		repository.save(member);

		// when
		int money = 80_000;
		repository.update(memberId, money);

		// then
		Member findeMember = repository.findById(memberId);
		assertThat(findeMember.getMoney()).isEqualTo(money);
	}

	@Test
	void delete() throws SQLException {
		// given
		String memberId = "memberV0";
		Member member = new Member(memberId, 10_000);
		repository.save(member);

		// when
		repository.delete(memberId);

		// then
		assertThatThrownBy(() -> repository.findById(memberId))
			.isInstanceOf(NoSuchElementException.class);
	}
}
