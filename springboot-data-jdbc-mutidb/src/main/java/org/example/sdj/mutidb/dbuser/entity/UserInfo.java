package org.example.sdj.mutidb.dbuser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * UserInfo
 *
 * @author auto generated
 * @date 2022-02-21 22:02:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_info")
public class UserInfo implements Serializable {

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
