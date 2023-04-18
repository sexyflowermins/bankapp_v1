package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.CustomPageException;

/**
 * 
 * View 렌더링을 위해 modelAndView
 * 객체를 반환 하도록 기본 설정 되어 있다.
 * 예외 페이지 리턴하도록 설정
 */
@ControllerAdvice
public class MyPageExceptionHandler {

	
	//사용자 정의 클래스 활용
	@ExceptionHandler(CustomPageException.class)
	public ModelAndView handleRuntimePageException(CustomPageException e) {
		// ModelAndView 활용 방법
		ModelAndView modelAndView = new ModelAndView("error");
		modelAndView.addObject("statusCode",HttpStatus.NOT_FOUND.value());
		modelAndView.addObject("message",e.getMessage());
		return modelAndView;
	}
}
