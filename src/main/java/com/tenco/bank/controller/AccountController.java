package com.tenco.bank.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.TransferFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.dto.response.HistoryDto;
import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private HttpSession session;
	
	@Autowired
	private AccountService accountService;
	// http://localhost:8080/account/list
	// http://localhost:8080/account/
	/**
	 * 계좌 목록 페이지
	 * 
	 * @return 목록 페이지 이동
	 */
	@GetMapping({ "/list", "/" })
	public String list(Model model) {

		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		//1.
		List<Account> accountList = accountService.readAccountList(principal.getId());
		if(accountList.isEmpty()) {
			model.addAttribute("accountList",null);			
		}else {
			model.addAttribute("accountList",accountList);						
		}
		//View 화면으로 데이터를 내려주는기술
		//model과 modelAndView 
		return "/account/list";
	}

	// 출금 페이지
	@GetMapping("/withdraw")
	public String withdraw() {
		return "/account/withdrawForm";
	}
	//출금처리 기능
	@PostMapping("/withdraw-proc")
	public String withdrawProc(WithdrawFormDto withdrawFormDto) {
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		if(withdrawFormDto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력 하세요", HttpStatus.BAD_REQUEST); 			
		}
		
		if(withdrawFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("출금 금액이 0원 이하일순 없습니다", HttpStatus.BAD_REQUEST); 
		}
		
		if(withdrawFormDto.getWAccountNumber() == null || withdrawFormDto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력 해주세요", HttpStatus.BAD_REQUEST); 
		}
		
		if(withdrawFormDto.getWAccountPassword() == null || withdrawFormDto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfullException("계좌 비밀번호를 입력 해주세요", HttpStatus.BAD_REQUEST); 
		}

		accountService.updateAccountWithdraw(withdrawFormDto,principal.getId());
		
		return "redirect:/account/list";
	}
	
	

	@GetMapping("/deposit")
	public String deposit() {		
		return "/account/depositForm";
	}
	
	@PostMapping("/deposit-proc")
	public String depositProc(DepositFormDto depositFormDto) {
		
		if(depositFormDto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력 해주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(depositFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("입금 금액이 0원 이하 일순 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		if(depositFormDto.getDAccountNumber() == null || depositFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		accountService.updateAccountDeposit(depositFormDto);
		
		return "redirect:/account/list";
	}
	
	
	
	@GetMapping("/transfer")
	public String transfer() {

		return "/account/transferForm";
	}

	@PostMapping("/transfer-proc")
	public String transterProc(TransferFormDto transferFormDto) {
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		//출금 계좌 번호 입력 여부
		if(transferFormDto.getWAccountNumber() == null || transferFormDto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("출금 계좌 번호를 확인해주세요", HttpStatus.BAD_REQUEST);			
		}
		//입금 계좌 번호 입력 여부
		if(transferFormDto.getDAccountNumber() == null || transferFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfullException("입금 계좌 번호를 확인해주세요", HttpStatus.BAD_REQUEST);			
		}

		//출금 계좌 비밀 번호 입력 여부 확인
		if(transferFormDto.getWAccountPassword() == null || transferFormDto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfullException("출금 계좌 비밀 번호를 확인해주세요", HttpStatus.BAD_REQUEST);			
		}
		//이체 금액 0원 이상 확인
		if(transferFormDto.getAmount() <= 0) {
			throw new CustomRestfullException("금액을 확인해주세요 0원 안되요", HttpStatus.BAD_REQUEST);			
		}
		//출금 계좌 입금 계좌 동일여부 확인
		if(transferFormDto.getWAccountNumber().equals(transferFormDto.getDAccountNumber())) {
			throw new CustomRestfullException("출금 계좌와 입금 계좌가 동일할수 없습니다", HttpStatus.UNAUTHORIZED);			
		}
		// 서비스 호출
		accountService.updateAccountTransfer(transferFormDto, principal.getId());
		return "redirect:/account/list";
	}
	
	
	@GetMapping("/save")
	public String save() {
		//인증검사 처리

		return "/account/saveForm";
	}
	
	/**
	 * 계좌 생성 
	 * 인증 검사, 유효성 검사 처리
	 * 0원 입력 가능, 마이너서 입력 불가
	 * @param saveFormDto
	 * @return
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		//유효성 검사 하기
		if(saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력 해 주세요", HttpStatus.BAD_REQUEST);
		}
		if(saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("계좌 비밀번호를 입력 해 주세요", HttpStatus.BAD_REQUEST);
		}
		if(saveFormDto.getBalance() < 0 || saveFormDto.getBalance() == null) {
			throw new CustomRestfullException("잘못된 금액 입니다", HttpStatus.BAD_REQUEST);			
		}
		// 서비스 호출
		accountService.createAccount(saveFormDto, principal.getId());
		
		return "redirect:/account/list";
	}
	
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Integer id,@RequestParam(name = "type" ,defaultValue = "all", required = false) String type, Model model) {
		
		User principal = (User)session.getAttribute(Define.PRINCIPAL);

		Account account = accountService.readAccount(id);
		List<HistoryDto> historyList = accountService.readHistoryListByAccount(type, id);
		//화면을 구성하기 위해 필요한 데이터를
		//소유자 이름
		// 계좌 번호 1개 계좌 잔액
		// 거래 내역
		model.addAttribute("principal", principal);
		model.addAttribute("account", account);
		model.addAttribute("historyList",historyList);
		// 거래 내역 결과 ㅈ집합 서비스,메서드
		return "/account/detail";
	}

}
