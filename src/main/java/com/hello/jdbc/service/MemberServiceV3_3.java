package com.hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.transaction.annotation.Transactional;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
	private final MemberRepositoryV3 repository;

	@Transactional
	public void accountTransfer(String fromId, String toId, int money) {
		try {
			bizLogic(fromId, toId, money);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void bizLogic(String fromId, String toId, int money) throws SQLException {
		Member fromMember = repository.findById(fromId);
		Member toMember = repository.findById(toId);

		repository.update(fromId, fromMember.getMoney() - money);
		validation(toMember);
		repository.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}
}
