package com.hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.hello.jdbc.connection.DBConnectionUtil;
import com.hello.jdbc.domain.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRepositoryV0 {

	public Member findById(String memberId) throws SQLException {
		String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			con = getConnection();
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
			close(con, psmt, rs);
		}
	}

	public void update(String memberId, int money) throws SQLException {
		String sql = "UPDATE member SET money=? WHERE member_id = ?";

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, money);
			pstmt.setString(2, memberId);
			int resultSize = pstmt.executeUpdate();
			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("DB Error", e);
			throw e;
		} finally {
			// 쿼리 실행 후 리소스 정리
			close(con, pstmt, null);
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
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("ResultSet Close Error", e);
			}
		}

		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				log.error("PreparedStatement Close Error", e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("Connection Close Error", e);
			}
		}
	}

	private Connection getConnection() {
		return DBConnectionUtil.getConnection();
	}
}
