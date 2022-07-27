package com.narangnorang.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.narangnorang.config.auth.PrincipalDetails;
import com.narangnorang.dto.MemberDTO;
import com.narangnorang.service.MemberService;

@RestController
public class MemberController {

	@Autowired
	MemberService memberService;
	@Autowired
	JavaMailSender javaMailSender;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	// 로그인
	@PostMapping("/api/login")
	public MemberDTO login(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> map) throws Exception {
//		MemberDTO memberDTO = memberService.selectMember(map);
//		session.setAttribute("login", memberDTO);
//		return memberDTO;
		return principalDetails.getMemberDTO();
	}

	// 로그아웃
	@GetMapping("/api/logout")
	public boolean logout(HttpSession session) throws Exception {
		session.invalidate();
		return true;
	}

	// 세션 만료
//	@GetMapping("/sessionInvalidate")
//	public String sessionInvalidate() throws Exception {
//		return "common/sessionInvalidate";
//	}

	// 일반회원가입 처리
	@PostMapping("/api/generalSignUp")
	public int insertGeneral(MemberDTO memberDTO) throws Exception {
		String rawPassWord = memberDTO.getPassword();
		String encPassWord = bCryptPasswordEncoder.encode(rawPassWord);
		memberDTO.setPassword(encPassWord);
		return memberService.generalSignUp(memberDTO);
	}

	// 상담사 회원가입 처리
	@PostMapping("/api/counselorSignUp")
	public int insertCounselor(MemberDTO memberDTO) throws Exception {
		String rawPassWord = memberDTO.getPassword();
		String encPassWord = bCryptPasswordEncoder.encode(rawPassWord);
		memberDTO.setPassword(encPassWord);
		return memberService.counselorSignUp(memberDTO);
	}

	// 로그인 세션 불러오기
	@GetMapping("/api/loginSession")
	public MemberDTO loginSession(HttpSession session) throws Exception {
		return (MemberDTO) session.getAttribute("login");
	}

	// 비번 찾기 임시 세션
	@GetMapping("/api/findPwSession")
	public MemberDTO findPwSession(HttpSession session) throws Exception {
		return (MemberDTO) session.getAttribute("findPw");
	}

	// Forgot Password?
	@PostMapping("/api/findPw")
	public int findPw(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		String email = memberDTO.getEmail();
		MemberDTO mdto = memberService.selectByEmail(email);
		session.setAttribute("findPw", mdto);
		return memberService.checkId(email);
	}

	// 새 비번 변경
	@PutMapping("/api/newPw")
	public int newPw(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("findPw");
		String rawPassWord = memberDTO.getPassword();
		String encPassWord = bCryptPasswordEncoder.encode(rawPassWord);
		mDTO.setPassword(encPassWord);
		return memberService.newPw(mDTO);
	}

	@PutMapping("/api/myPage/newPw")
	public int myPageNewPw(Authentication authentication, HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		String email = authentication.getName();
		MemberDTO mDTO = memberService.selectByEmail(email);
//		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		String rawPassWord = memberDTO.getPassword();
		String encPassWord = bCryptPasswordEncoder.encode(rawPassWord);
		mDTO.setPassword(encPassWord);
//		mDTO.setPassword(memberDTO.getPassword());
//		session.setAttribute("login", mDTO);
		return memberService.newPw(mDTO);
	}

	// 일반회원 정보 수정
	@PutMapping("/api/generalEdit")
	public int generalEdit(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		memberDTO.setId(mDTO.getId());
		memberDTO.setPassword(mDTO.getPassword());
		memberDTO.setPrivilege(mDTO.getPrivilege());
		memberDTO.setDatetime(mDTO.getDatetime());
		memberDTO.setPoint(mDTO.getPoint());
		session.setAttribute("login", memberDTO);
		return memberService.generalEdit(memberDTO);
	}

	// 상담사회원 정보 수정
	@PutMapping("/api/counselorEdit")
	public int counselorEdit(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		memberDTO.setId(mDTO.getId());
		memberDTO.setPassword(mDTO.getPassword());
		memberDTO.setPrivilege(mDTO.getPrivilege());
		memberDTO.setDatetime(mDTO.getDatetime());
		session.setAttribute("login", memberDTO);
		return memberService.counselorEdit(memberDTO);
	}

	// 프로필 사진 수정
	@PutMapping("/api/photoUpdate")
	public void photoUpdate(HttpSession session, HttpServletRequest request, @RequestParam MultipartFile mFile) throws Exception {
		String uploadPath = request.getSession().getServletContext().getRealPath("/")
				.concat("resources/images/member/");
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		try {
			File file = new File(uploadPath + mDTO.getId());
			file.delete();

			String newName = String.valueOf(mDTO.getId());
			mFile.transferTo(new File(uploadPath + newName + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 관리자 페이지 - 회원 관리
	@GetMapping(value = "/api/memberManagement")
	public HashMap<String, Object> getAllLists() throws Exception {
		HashMap<String, Object> result = new HashMap<>();
		result.put("memberDTO", memberService.selectAll());
		return result;
	}

	// 관리자 페이지 - 선택 계정 삭제
	@DeleteMapping("/api/delMember")
	public int delMember(@RequestParam Map<String, String> map) throws Exception {
		Collection coll = map.values();
		List<String> list = new ArrayList<String>(coll);
		return memberService.delSelected(list);
	}

	// 관리자 페이지 - 상담사 권한 관리
	@GetMapping("/api/counselorPrivilege")
	public HashMap<String, Object> counselorPrivilege() throws Exception {
		HashMap<String, Object> result = new HashMap<>();
		result.put("memberDTO", memberService.selectByPrivileage2());
		return result;
	}

	// 관리자 페이지 - 상담사 권한 UP
	@PutMapping("/api/privilegeUp")
	public int privileageUp(@RequestParam Map<String, String> map) throws Exception {
		Collection coll = map.values();
		List<String> list = new ArrayList<String>(coll);
		return memberService.privileageUp(list);
	}

	// 아이디 중복 체크
	@PostMapping("/api/checkEmail")
	public int checkEmail(@RequestBody MemberDTO dto) throws Exception {
		return memberService.checkId(dto.getEmail());
	}

	// 닉네임 중복 체크
	@PostMapping("/api/checkName")
	public int checkNickname(@RequestBody MemberDTO dto) throws Exception {
		return memberService.checkNickname(dto.getName());
	}

	// 인증 이메일
	@PostMapping("/api/sendMail")
	public void sendMail(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception{
		String email = memberDTO.getEmail();
		Random random = new Random();  //난수 생성을 위한 랜덤 클래스
		String key="";  //인증번호 

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email); // 스크립트에서 보낸 메일을 받을 사용자 이메일 주소
		// 입력 키를 위한 코드
		for (int i = 0; i < 3; i++) {
			int index = random.nextInt(25) + 65; // A~Z까지 랜덤 알파벳 생성
			key += (char) index;
		}
		int numIndex = random.nextInt(9999) + 1000; // 4자리 랜덤 정수를 생성
		key += numIndex;
		message.setSubject("인증번호 입력을 위한 메일 전송");
		message.setText("인증 번호 : " + key);
		javaMailSender.send(message);
		session.setAttribute("key", key);
	}

	// 인증번호 확인
	@PostMapping("/api/compare")
	public boolean compare(HttpSession session, @RequestBody Map<String, String> map) throws Exception {
		if(session.getAttribute("key").equals(map.get("com"))){
			return true;
		} else {
			return false;
		}
	}
	
	@GetMapping("/api/kakaologin")
	public HashMap<String, Object> kakaologin(String code) throws Exception {
		String access_token = memberService.getKakaoAccessToken(code);
		HashMap<String, String> userinfo = memberService.getKakaoUserInfo(access_token);
		MemberDTO memberDTO = (MemberDTO)memberService.selectByKakaoId(userinfo.get("id"));
		
		HashMap<String, Object> map = new HashMap<>();
		// 카카오 회원가입 되어있지 않을 시
		if (memberDTO == null) {
			map.put("userinfo", userinfo);
			map.put("result", 0);
		}
		// 카카오 회원가입 되어있을 시 (바로 로그인) 
		else {
			memberDTO.setPassword(userinfo.get("id"));
			map.put("memberDTO", memberDTO);
			map.put("result", 1);
		}
		
		return map;
	}
	
	// 카카오 회원가입 처리
	@PostMapping("/api/kakaoSignUp")
	public int insertKakaoUser(MemberDTO memberDTO) throws Exception {
		int randomNumber = 0;
		String tmpId = "";
		Boolean idDuplication = true;
		while (idDuplication) {
			randomNumber = (int)Math.floor(Math.random() * (99999999 - 10000000 + 1)) + 99999999;
			tmpId = "kakao" + randomNumber + "@k.com";
			if (memberService.checkId(tmpId) == 0) {
				memberDTO.setEmail(tmpId);
				idDuplication = false;
			}
		}
		String tmpPwd = memberDTO.getKakaoId();
		String encPassWord = bCryptPasswordEncoder.encode(tmpPwd);
		memberDTO.setPassword(encPassWord);
		return memberService.generalSignUp(memberDTO);
	}

	@PostMapping("/api/googleLogin")
	public String googleLogin() throws Exception {
		return "sibal";
	}

	@GetMapping("/api/naverLogin")
	public HashMap<String,Object> naverLogin(@RequestParam String access_token) throws Exception {
		System.out.println(access_token);
		String map = memberService.getNaverUserInfo(access_token);
		HashMap<String, Object> retMap = new Gson().fromJson(
				map, new TypeToken<HashMap<String, Object>>() {}.getType()
		);
		String NaverInfo = (String) retMap.get("response");
		System.out.println(retMap.get("response"));
		HashMap<String, Object> response = new Gson().fromJson(
				NaverInfo, new TypeToken<HashMap<String, Object>>() {}.getType()
		);
		String email = (String) response.get("email");
		String name = (String) response.get("name");

		System.out.println(email);
		HashMap<String,Object> mDTO = new HashMap<String,Object>();
		mDTO.put("email",email);
		mDTO.put("name",name);

		System.out.println(mDTO);

//		MemberDTO memberDTO = (MemberDTO)memberService.selectByKakaoId(userinfo.get("id"));

//		// 카카오 회원가입 되어있지 않을 시
//		if (memberDTO == null) {
//
//		}
//		// 카카오 회원가입 되어있을 시 (바로 로그인)
//		else {
//
//		}
		return mDTO;
	}
	// 에러 처리
	@ExceptionHandler({ Exception.class })
	public String error() throws Exception {
		return "error";
	}

}
