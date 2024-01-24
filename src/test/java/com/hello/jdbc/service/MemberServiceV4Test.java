package com.hello.jdbc.service;

import static com.hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.IMemberRepository;
import com.hello.jdbc.repository.MemberRepositoryV5;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {
	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private IMemberRepository memberRepository;
	@Autowired
	private IMemberService memberService;

	@Autowired
	private DataSource dataSource;

	@TestConfiguration
	static class TestConfig {
		@Bean
		DataSource dataSource() {
			return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		}

		@Bean
		PlatformTransactionManager transactionManager() {
			return new DataSourceTransactionManager(dataSource());
		}

		@Bean
		IMemberRepository memberRepository() {
			return new MemberRepositoryV5(dataSource());
		}

		@Bean
		IMemberService memberService() {
			return new MemberServiceV4(memberRepository());
		}

	}

	@BeforeEach
	void before() throws SQLException {
		Connection connection = dataSource.getConnection();
		PreparedStatement pst = connection.prepareStatement("DELETE FROM member");

		pst.executeUpdate();

		pst.close();
		connection.close();
	}

	@Test
	void AopCheck() {
		log.info("memberService class={}", memberService.getClass());
		log.info("memberRepository class={}", memberRepository.getClass());
		assertThat(AopUtils.isAopProxy(memberService)).isTrue();
		assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws SQLException {
		// given
		Member memberA = new Member(MEMBER_A, 10_000);
		Member memberB = new Member(MEMBER_B, 10_000);
		memberRepository.save(memberA);
		memberRepository.save(memberB);

		// when
		memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

		// then
		Member findMemberA = memberRepository.findById(memberA.getMemberId());
		Member findMemberB = memberRepository.findById(memberB.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(8_000);
		assertThat(findMemberB.getMoney()).isEqualTo(12_000);
	}

	@Test
	@DisplayName("이체중 예외 발생")
	void accountTransferEx() throws SQLException {
		// given
		Member memberA = new Member(MEMBER_A, 10_000);
		Member memberB = new Member(MEMBER_EX, 10_000);
		memberRepository.save(memberA);
		memberRepository.save(memberB);

		// when
		assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000))
			.isInstanceOf(IllegalStateException.class);

		// then
		Member findMemberA = memberRepository.findById(memberA.getMemberId());
		Member findMemberB = memberRepository.findById(memberB.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(10_000);
		assertThat(findMemberB.getMoney()).isEqualTo(10_000);
	}
}
