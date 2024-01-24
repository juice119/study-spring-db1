package com.hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberServiceV3_2 {
	private final TransactionTemplate txTemplate;
	private final MemberRepositoryV3 repository;

	public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 repository) {
		this.txTemplate = new TransactionTemplate(transactionManager);
		this.repository = repository;
	}

	public void accountTransfer(String fromId, String toId, int money) {
		// 트랜잭션 시작
		txTemplate.executeWithoutResult(status -> {
			try {
				bizLogic(fromId, toId, money);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
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
