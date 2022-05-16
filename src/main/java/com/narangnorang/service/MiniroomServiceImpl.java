package com.narangnorang.service;

import java.util.List;

import com.narangnorang.dto.MyItemDTO;
import org.springframework.stereotype.Service;

import com.narangnorang.dao.MiniroomDAO;
import com.narangnorang.dao.PostDAO;
import com.narangnorang.dto.ItemDTO;
import com.narangnorang.dto.PostDTO;


@Service("miniroomService")
public class MiniroomServiceImpl implements MiniroomService {

	MiniroomDAO miniroomDAO;
	//생성자 주입
	public MiniroomServiceImpl(MiniroomDAO dao) {
		this.miniroomDAO = dao;
	}
	@Override
	public List<ItemDTO> selectAllItems(String category){
		List<ItemDTO> list = miniroomDAO.selectAllItems(category);
		return list;
	}

	@Override
	public int insert(MyItemDTO myItemDTO) {

		return miniroomDAO.insert(myItemDTO);
	}

	@Override
	public int update(MyItemDTO myItemDTO) {
		return miniroomDAO.update(myItemDTO);
	}

	@Override
	public MyItemDTO selectByMyItemId(int itemId) {
		MyItemDTO myItemDTO = miniroomDAO.selectByMyItemId(itemId);
		return myItemDTO;
	}

	@Override
	public List<MyItemDTO> selectAllMyItems(String category) {
		List<MyItemDTO> list = miniroomDAO.selectAllMyItems(category);

		return list;
	}


}
