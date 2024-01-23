package com.hello.jdbc.repository;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.hello.jdbc.domain.Member;

class MemberRepositoryV0Test {

	private final MemberRepositoryV0 repository = new MemberRepositoryV0();

	@Test
	void save() throws SQLException {
		Member member = new Member("memberV0", 10_000);
		repository.save(member);
	}
}
