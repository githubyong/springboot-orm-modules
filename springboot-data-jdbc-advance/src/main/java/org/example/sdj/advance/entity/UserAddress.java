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
 * UserAddress
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_address")
public class UserAddress extends BaseEntity implements Serializable {

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
