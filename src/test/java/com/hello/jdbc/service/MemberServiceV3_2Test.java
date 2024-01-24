package com.hello.jdbc.service;

import static com.hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;

class MemberServiceV3_2Test {
	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";
	private MemberRepositoryV3 memberRepository;
	private MemberServiceV3_2 memberService;

	@BeforeEach
	void before() throws SQLException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,
			USERNAME, PASSWORD);
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		memberRepository = new MemberRepositoryV3(dataSource);
		memberService = new MemberServiceV3_2(transactionManager, memberRepository);

		Connection connection = dataSource.getConnection();
		PreparedStatement pst = connection.prepareStatement("DELETE FROM member");

		pst.executeUpdate();

		pst.close();
		connection.close();
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
