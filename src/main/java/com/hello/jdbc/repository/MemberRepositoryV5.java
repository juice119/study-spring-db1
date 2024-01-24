package com.hello.jdbc.repository;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.hello.jdbc.domain.Member;

import lombok.extern.slf4j.Slf4j;

/**
 * JdbcTemplate 사용
 */
@Slf4j
public class MemberRepositoryV5 implements IMemberRepository {

	private final JdbcTemplate jdbcTemplate;

	public MemberRepositoryV5(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Member save(Member member) {
		String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";
		jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
		return member;
	}

	public Member findById(String memberId) {
		String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

		return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
	}

	public void update(String memberId, int money) {
		String sql = "UPDATE member SET money=? WHERE member_id = ?";
		jdbcTemplate.update(sql, money, memberId);
	}

	public void delete(String memberId) {
		String sql = "DELETE FROM member WHERE member_id = ?";
		jdbcTemplate.update(sql, memberId);
	}

	private RowMapper<Member> memberRowMapper() {
		return (rs, rowNum) -> {
			Member member = new Member();
			member.setMemberId(rs.getString("member_id"));
			member.setMoney(rs.getInt("money"));
			return member;
		};
	}
}
