package com.narangnorang.controller;

import java.io.File;
import java.lang.reflect.Member;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.narangnorang.dto.MyRoomDTO;
import com.narangnorang.service.MiniroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.narangnorang.dto.MemberDTO;
import com.narangnorang.service.MemberService;
import com.narangnorang.service.MessageService;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;
	@Autowired
	JavaMailSender javaMailSender;

	// 로그인
	@PostMapping("/api/login")
	@ResponseBody
	public MemberDTO login(HttpSession session, @RequestParam Map<String, String> map) throws Exception {
		MemberDTO memberDTO = memberService.selectMember(map);
		session.setAttribute("login", memberDTO);
		return memberDTO;
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) throws Exception {
		session.invalidate();
		return "redirect:/main";
	}

	// 세션 만료
	@GetMapping("/sessionInvalidate")
	public String sessionInvalidate() throws Exception {
		return "common/sessionInvalidate";
	}

	// 일반회원가입 처리
	@PostMapping("/api/generalSignUp")
	@ResponseBody
	public int insertGeneral(MemberDTO memberDTO) throws Exception {
		return memberService.generalSignUp(memberDTO);
	}

	// 상담사 회원가입 처리
	@PostMapping("/api/counselorSignUp")
	@ResponseBody
	public int insertCounselor(MemberDTO memberDTO) throws Exception {
		return memberService.counselorSignUp(memberDTO);
	}

	// 로그인 세션 불러오기
	@GetMapping("/api/loginSession")
	@ResponseBody
	public MemberDTO mypage(HttpSession session) throws Exception {
		return (MemberDTO) session.getAttribute("login");
	}

	// 새 비번 변경
//	@PutMapping("/newPw")
//	@ResponseBody
//	public int newPw(MemberDTO memberDTO) throws Exception {
//		return memberService.newPw(memberDTO);
//	}

	// 새 비번 변경
	@PutMapping("/api/myPage/newPw")
	@ResponseBody
	public int myPageNewPw(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		memberDTO.setId(mDTO.getId());
		memberDTO.setEmail(mDTO.getEmail());
		memberDTO.setPrivilege(mDTO.getPrivilege());
		memberDTO.setName(mDTO.getName());
		memberDTO.setPhone(mDTO.getPhone());
		memberDTO.setDatetime(mDTO.getDatetime());
		memberDTO.setPhoto(mDTO.getPhoto());
		memberDTO.setRegion(mDTO.getRegion());
		memberDTO.setPoint(mDTO.getPoint());
		memberDTO.setZipcode(mDTO.getZipcode());
		memberDTO.setAddress1(mDTO.getAddress1());
		memberDTO.setAddress2(mDTO.getAddress2());
		memberDTO.setAddress3(mDTO.getAddress3());
		memberDTO.setJob(mDTO.getJob());
		memberDTO.setIntroduction(mDTO.getIntroduction());
		session.setAttribute("login", memberDTO);
		return memberService.newPw(memberDTO);
	}

	// 일반회원 정보 수정
	@PutMapping("/api/generalEdit")
	@ResponseBody
	public int generalEdit(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		memberDTO.setId(mDTO.getId());
		memberDTO.setPassword(mDTO.getPassword());
		memberDTO.setPrivilege(mDTO.getPrivilege());
		memberDTO.setDatetime(mDTO.getDatetime());
		memberDTO.setPhoto(mDTO.getPhoto());
		memberDTO.setPoint(mDTO.getPoint());
		session.setAttribute("login", memberDTO);
		return memberService.generalEdit(memberDTO);
	}

	// 상담사회원 정보 수정
	@PutMapping("/api/counselorEdit")
	@ResponseBody
	public int counselorEdit(HttpSession session, @RequestBody MemberDTO memberDTO) throws Exception {
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		memberDTO.setId(mDTO.getId());
		memberDTO.setPassword(mDTO.getPassword());
		memberDTO.setPrivilege(mDTO.getPrivilege());
		memberDTO.setDatetime(mDTO.getDatetime());
		memberDTO.setPhoto(mDTO.getPhoto());
		session.setAttribute("login", memberDTO);
		return memberService.counselorEdit(memberDTO);
	}

	// 프로필 사진 수정
	@PutMapping("/api/photoUpdate")
	public int photoUpdate(HttpSession session, @RequestParam MultipartFile mFile) throws Exception {
		String uploadPath = "C:/HighFive/narangnorang_frontend/src/assets/member/";
		MemberDTO mDTO = (MemberDTO) session.getAttribute("login");
		System.out.println(mFile);
		int cnt = 0;
		try {
			if (mDTO.getPhoto() != null) {
				File file = new File(uploadPath + mDTO.getPhoto());
				file.delete();
			}
			String newName = String.valueOf(mDTO.getId());
			mFile.transferTo(new File(uploadPath + newName + ".png"));

			mDTO.setPhoto(newName);
			cnt = memberService.photoUpdate(mDTO);
			session.setAttribute("login", mDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cnt;
	}

	// 관리자 페이지 - 회원 관리
	@GetMapping(value = "/api/memberManagement")
	@ResponseBody
	public HashMap<String, Object> getAllLists() throws Exception {
		HashMap<String, Object> result = new HashMap<>();
		result.put("memberDTO", memberService.selectAll());
		return result;
	}

	// 관리자 페이지 - 선택 계정 삭제
	@DeleteMapping("/api/delMember")
	@ResponseBody
	public int delMember(@RequestParam Map<String, String> map) throws Exception {
		Collection coll = map.values();
		List<String> list = new ArrayList<String>(coll);
		return memberService.delSelected(list);
	}

	// 관리자 페이지 - 상담사 권한 관리
//	@GetMapping("/admin/counselPrivilege2")
//	@ResponseBody
//	public ModelAndView getPrivileage2() throws Exception {
//		List<MemberDTO> lists = memberService.selectByPrivileage2();
//		ModelAndView mav = new ModelAndView("member/counselPrivilege2");
//		mav.addObject("lists", lists);
//		return mav;
//	}

	// 관리자 페이지 - 상담사 권한 UP
//	@GetMapping("/admin/privilegeUp")
//	public String privileageUp(HttpServletRequest request) throws Exception {
//		String nextPage = "";
//		String[] check = request.getParameterValues("check");
//		if (check == null) {
//			nextPage = "member/upFail";
//		} else {
//			List<String> list = Arrays.asList(check);
//			memberService.privileageUp(list);
//			nextPage = "redirect:/admin/counselPrivilege2";
//		}
//		return nextPage;
//	}

	// 아이디 중복 체크
	@PostMapping("/api/checkEmail")
	@ResponseBody
	public int checkEmail(@RequestBody MemberDTO dto) throws Exception {
		return memberService.checkId(dto.getEmail());
	}

	// 닉네임 중복 체크
	@PostMapping("/api/checkName")
	@ResponseBody
	public int checkNickname(@RequestBody MemberDTO dto) throws Exception {
		return memberService.checkNickname(dto.getName());
	}

	// 인증 이메일
	@PostMapping("/api/sendMail")
	public String sendMail(@RequestBody MemberDTO memberDTO) throws Exception{
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
		return key;
	}

	// 에러 처리
	@ExceptionHandler({ Exception.class })
	public String error() throws Exception {
		return "error";
	}

}
