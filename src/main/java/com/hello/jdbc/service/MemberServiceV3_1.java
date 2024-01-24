package com.hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceV3_1 {
	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 repository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 시작
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		// 비즈니스 로직
		try {
			Member fromMember = repository.findById(fromId);
			Member toMember = repository.findById(toId);

			repository.update(fromId, fromMember.getMoney() - money);
			validation(toMember);
			repository.update(toId, toMember.getMoney() + money);
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}
}
