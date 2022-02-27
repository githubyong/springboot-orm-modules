package org.example.sdj.advance.invalid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sdj.advance.entity.OrderItem;
import org.example.sdj.advance.support.entity.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
public class OrderInfoWithItems extends BaseEntity implements Serializable {

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

//	@MappedCollection
	@MappedCollection(idColumn = "ORDER_SN",keyColumn ="ID" )	//  [SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ORDER_SN" = ? ORDER BY "ORDER_SN"]
//	@MappedCollection(idColumn = "ID",keyColumn ="ORDER_SN" )//  [SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ID" = ? ORDER BY "ORDER_SN"] order_item.ID
		List<OrderItem> orderItems;
}
