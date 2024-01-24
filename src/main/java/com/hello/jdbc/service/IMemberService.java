package com.hello.jdbc.service;

public interface IMemberService {
	void accountTransfer(String fromId, String toId, int money);
}
