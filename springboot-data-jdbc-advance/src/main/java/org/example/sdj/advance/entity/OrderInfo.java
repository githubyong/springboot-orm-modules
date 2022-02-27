package org.example.sdj.advance.entity;

import org.example.sdj.advance.support.entity.BaseEntity;
import org.example.sdj.advance.support.entity.CustomField;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * OrderInfo
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_info")
public class OrderInfo extends BaseEntity implements Serializable {

	@Id
	@Column("ID")
	private Long id;

	@Column("ORDER_SN")
	private String orderSn;

	@Column("MEMBER_USERNAME")
	private String memberUsername;

	@Column("TOTAL_AMOUNT")
	private BigDecimal totalAmount;

	@Column("ORDER_TYPE")
	private Integer orderType;

	@Column("STATUS")
	private Integer status;

	@Column("PAY_TYPE")
	private Integer payType;

	@Column("RECEIVER_PHONE")
	private String receiverPhone;

	@Column("RECEIVER_DETAIL_ADDRESS")
	private String receiverDetailAddress;

	@Column("DELIVERY_SN")
	private String deliverySn;

	@Column("CREATE_TIME")
	private Date createTime;
}
