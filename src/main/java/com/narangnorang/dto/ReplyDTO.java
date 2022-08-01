package com.narangnorang.dto;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("ReplyDTO")
public class ReplyDTO {

	private int id;
	private int memberId;
	private String memberName;
	private int postId;
	private String content;
	private String datetime;
	private int likes;

}
