package com.hello.jdbc.service;

import org.springframework.transaction.annotation.Transactional;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.IMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 implements IMemberService {
	private final IMemberRepository repository;

	@Transactional
	public void accountTransfer(String fromId, String toId, int money) {
		bizLogic(fromId, toId, money);
	}

	private void bizLogic(String fromId, String toId, int money) {
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
