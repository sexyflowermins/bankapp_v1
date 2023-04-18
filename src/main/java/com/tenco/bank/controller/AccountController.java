package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfullException;

@Controller
@RequestMapping("/account")
public class AccountController {
	// todo
	// 1. 계좌 목록 페이지
	// 2. 입금 페이지
	// 3. 출금 페이지
	// 4. 이체 페이지
	// 5. 계좌 상태보기 페이지
	// 6. 계죄 생성 페이지

	// http://localhost:8080/account/list
	// http://localhost:8080/account/
	/**
	 * 계좌 목록 페이지
	 * 
	 * @return 목록 페이지 이동
	 */
	@GetMapping({ "/list", "/" })
	public String list() {
	
		return "/account/list";
	}

	// 출금 페이지
	@GetMapping("/withdraw")
	public String withdraw() {

		return "/account/withdrawForm";
	}

	@GetMapping("/deposit")
	public String deposit() {
		return "/account/depositForm";
	}

	@GetMapping("/transfer")
	public String transfer() {
		return "/account/transferForm";
	}

	@GetMapping("/save")
	public String save() {
		return "/account/saveForm";
	}

	@GetMapping("/detail")
	public String detail() {
		return "";
	}

}
