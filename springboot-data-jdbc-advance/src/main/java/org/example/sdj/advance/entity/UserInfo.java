package org.example.sdj.advance.entity;

import org.example.sdj.advance.support.entity.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.util.Date;

/**
 * UserInfo
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_info")
public class UserInfo extends BaseEntity implements Serializable {

	@Id
	@Column("ID")
	private Long id;

	@Column("LOGIN_NAME")
	private String loginName;

	@Column("PASWORD")
	private String pasword;

	@Column("NICK_NAME")
	private String nickName;

	@Column("EMAIL")
	private String email;

	@Column("TELEPHONE")
	private String telephone;

	@Column("CREATE_TIME")
	private Date createTime;

	@Column("UPDATE_TIME")
	private Date updateTime;
}
