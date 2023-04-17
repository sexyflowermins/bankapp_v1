package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.model.History;

@Mapper // 반드시 지정하기 의존성
public interface HistoryRepository {
	
	public int insert(History histroy);
	public int updateById(History histroy);
	public int deleteById(int id);
	public History findById(int id);
	public List<History> findAll();
	
}
