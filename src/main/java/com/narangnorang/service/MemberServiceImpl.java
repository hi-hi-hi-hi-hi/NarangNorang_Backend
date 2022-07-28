package com.narangnorang.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.narangnorang.dao.MemberDAO;
import com.narangnorang.dao.MiniroomDAO;
import com.narangnorang.dto.MemberDTO;

@Service("memberService")
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberDAO memberDAO;
	@Autowired
	MiniroomDAO miniroomDAO;

	// 로그인
	@Transactional
	@Override
	public MemberDTO selectMember(Map<String, String> map) throws Exception {
		memberDAO.updateDatetime(map);
		return memberDAO.selectMember(map);
	}

	// 아이디 중복 체크
	@Override
	public int checkId(String email) throws Exception {
		return memberDAO.checkId(email);
	}

	// 닉네임 중복 체크
	@Override
	public int checkNickname(String nickname) throws Exception {
		return memberDAO.checkNickname(nickname);
	}

	// 일반회원가입
	@Transactional
	@Override
	public int generalSignUp(MemberDTO dto) throws Exception {
		String name = dto.getName();
		return memberDAO.generalSignUp(dto) & miniroomDAO.insertDefaultItems(name) & miniroomDAO.insertDefaultMyItems1() & miniroomDAO.insertDefaultMyItems2();
	}

	// 상담사 회원가입
	@Override
	public int counselorSignUp(MemberDTO dto) throws Exception {
		return memberDAO.counselorSignUp(dto);
	}

	// email로 회원 찾기
	@Override
	public MemberDTO selectByEmail(String email) throws Exception {
		return memberDAO.selectByEmail(email);
	}

	// 새 비밀번호 설정
	@Override
	public int newPw(MemberDTO dto) throws Exception {
		return memberDAO.newPw(dto);
	}

	// 일반회원 정보수정
	@Override
	public int generalEdit(MemberDTO dto) throws Exception {
		return memberDAO.generalEdit(dto);
	}

	// 상담사회원 정보수정
	@Override
	public int counselorEdit(MemberDTO dto) throws Exception {
		return memberDAO.counselorEdit(dto);
	}

	// 모든 회원
	@Override
	public List<MemberDTO> selectAll() throws Exception {
		return memberDAO.selectAll();
	}

	// 선택 계정 삭제
	@Override
	public int delSelected(List<String> list) throws Exception {
		return memberDAO.delSelected(list);
	}

	// 미승인 상담사 회원
	@Override
	public List<MemberDTO> selectByPrivileage2() throws Exception {
		return memberDAO.selectByPrivileage2();
	}

	// 선택 상담사 승급
	@Override
	public int privileageUp(List<String> list) throws Exception {
		return memberDAO.privileageUp(list);
	}

	@Override
	public MemberDTO selectByKakaoId(String kakaoId) throws Exception {
		return memberDAO.selectByKakaoId(kakaoId);
	}

}
