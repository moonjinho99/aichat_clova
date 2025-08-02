package com.example.aichat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aichat.entity.ChattingTest;

public interface ChattingTestRepository extends JpaRepository<ChattingTest, Long>{

	List<ChattingTest> findByMemberId(Long memberId);
	
}
