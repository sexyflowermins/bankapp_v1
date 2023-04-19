package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;

@Service //IoC 대상 싱글톤 으로 관리
public class AccountService {
	
	@Autowired //DI
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	/**
	 * 계좌 생성 기능
	 * @param saveFormDto
	 * @param principalId
	 */
	@Transactional
	public void createAccount(SaveFormDto saveFormDto,Integer principalId) {
		//SaveFormDto -> 변경, 신규 생성
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);
		int resultRowCount = accountRepository.insert(account);
		if(resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//계좌 목록 보기 기능
	@Transactional
	public List<Account> readAccountList(Integer userId){
		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}
	
	//출금 기능 구현
	//로직 고민 해 보기
	//1. 계좌 존재 여부 확인 select query
	//2. 본인 계좌 확인
	//3. 계좌 비번 확인
	//4. 잔액 여부 확인
	//5. 출금 처리 update query
	//6. 거래 내역 등록 insert query
	//7. 트랜젝션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto,Integer principalId) {
		
		Account accountEntity = accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
		System.out.println(accountEntity.toString());
		//1.
		if(accountEntity == null) {
			throw new CustomRestfullException("계좌가 없습니다", HttpStatus.BAD_REQUEST);
		}
		//2.
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다", HttpStatus.UNAUTHORIZED);
		} 
		//3
		if(accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다", HttpStatus.UNAUTHORIZED);
		}
		//4
		if(accountEntity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족합니다", HttpStatus.BAD_REQUEST);			
		}
		//5 모델 상태값 변경 처리
		accountEntity.withdraw(withdrawFormDto.getAmount());
		accountRepository.updateById(accountEntity);
		//6. 거래내역등록
		History history = new History();
		history.setAmount(withdrawFormDto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		int resultRowCount = historyRepository.insert(history);;
		if(resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지않음", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	
	}
	
}
