package com.hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.jdbc.support.JdbcUtils;

import com.hello.jdbc.domain.Member;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

	private final DataSource dataSource;

	public MemberRepositoryV2(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Member findById(Connection con, String memberId) throws SQLException {
		String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			psmt = con.prepareStatement(sql);
			psmt.setString(1, memberId);

			rs = psmt.executeQuery();
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
			throw e;
		} finally {
			// Transaction 을 위해서 커넥션 유지
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(psmt);
		}
	}

	public void update(Connection con, String memberId, int money) throws SQLException {
		String sql = "UPDATE member SET money=? WHERE member_id = ?";

		PreparedStatement pstmt = null;

		try {
			pstmt.setInt(1, money);
			pstmt.setString(2, memberId);
			int resultSize = pstmt.executeUpdate();
			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw e;
		} finally {
			// Transaction 을 위해서 커넥션 유지
			JdbcUtils.closeStatement(pstmt);
		}

	}

	public void delete(String memberId) throws SQLException {
		String sql = "DELETE FROM member WHERE member_id = ?";

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw e;
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, pstmt, null);
		}

	}

	public Member save(Member member) throws SQLException {
		String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member.getMemberId());
			pstmt.setInt(2, member.getMoney());
			pstmt.executeUpdate();
			return member;
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw e;
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, pstmt, null);
		}

	}

	private void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
		// 리소스 정리 역순으로 진행 / 쿼리 실행 과정(connection ->  PreparedStatement) / 정리 과정은 반대로 (PreparedStatement -> connection)
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(pstmt);
		JdbcUtils.closeConnection(con);
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
