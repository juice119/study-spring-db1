package com.hello.jdbc.repository;

import com.hello.jdbc.domain.Member;

public interface IMemberRepository {
	Member save(Member member);

	Member findById(String MemberId);

	void update(String memberId, int money);

	void delete(String memberId);
}
