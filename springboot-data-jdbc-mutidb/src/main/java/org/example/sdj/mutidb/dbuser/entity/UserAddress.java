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
 * UserAddress
 *
 * @author auto generated
 * @date 2022-02-21 22:02:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_address")
public class UserAddress implements Serializable {

	@Id
	@Column("ID")
	private Long id;

	@Column("USER_ID")
	private Integer userId;

	@Column("RECEIVER_NAME")
	private String receiverName;

	@Column("RECEIVER_PHONE")
	private String receiverPhone;

	@Column("RECEIVER_DETAIL_ADDRESS")
	private String receiverDetailAddress;

	@Column("USER_TAG")
	private String userTag;

	@Column("CREATE_TIME")
	private Date createTime;

	@Column("UPDATE_TIME")
	private Date updateTime;
}
