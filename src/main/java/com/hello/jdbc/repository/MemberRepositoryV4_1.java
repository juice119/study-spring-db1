package com.hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.exception.MyDbException;

import lombok.extern.slf4j.Slf4j;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements IMemberRepository {

	private final DataSource dataSource;

	public MemberRepositoryV4_1(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Member findById(String memberId) {
		String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, memberId);

			rs = statement.executeQuery();
			if (rs.next()) {
				Member member = new Member();
				member.setMemberId(rs.getString("member_id"));
				member.setMoney(rs.getInt("money"));
				return member;
			} else {
				throw new NoSuchElementException("member not found memberId=" + memberId);
			}
		} catch (SQLException e) {
			log.error("멤버 조회 에러", e);
			throw new MyDbException(e);
		} finally {
			close(con, statement, rs);
		}
	}

	public void update(String memberId, int money) {
		String sql = "UPDATE member SET money=? WHERE member_id = ?";

		Connection con = null;
		PreparedStatement statement = null;

		try {
			con = getConnection();
			statement = con.prepareStatement(sql);
			statement.setInt(1, money);
			statement.setString(2, memberId);
			int resultSize = statement.executeUpdate();
			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw new MyDbException(e);
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, statement, null);
		}

	}

	public void delete(String memberId) {
		String sql = "DELETE FROM member WHERE member_id = ?";

		Connection con = null;
		PreparedStatement statement = null;

		try {
			con = getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, memberId);
			statement.executeUpdate();
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw new MyDbException(e);
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, statement, null);
		}

	}

	public Member save(Member member) {
		String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";

		Connection con = null;
		PreparedStatement statement = null;

		try {
			con = getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, member.getMemberId());
			statement.setInt(2, member.getMoney());
			statement.executeUpdate();
			return member;
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw new MyDbException(e);
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, statement, null);
		}

	}

	private void close(Connection con, PreparedStatement statement, ResultSet rs) {
		// 리소스 정리 역순으로 진행 / 쿼리 실행 과정(connection ->  PreparedStatement) / 정리 과정은 반대로 (PreparedStatement -> connection)
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(statement);
		DataSourceUtils.releaseConnection(con, dataSource);
	}

	private Connection getConnection() throws SQLException {
		return DataSourceUtils.getConnection(dataSource);
	}
}
