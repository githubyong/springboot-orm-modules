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

import java.math.BigDecimal;

/**
 * OrderItem
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_item")
public class OrderItem extends BaseEntity implements Serializable {

	@Id
	@Column("ID")
	private Long id;

	@Column("ORDER_SN")
	private String orderSn;

	@Column("PRODUCT_ID")
	private Long productId;

	@Column("PRODUCT_NAME")
	private String productName;

	@Column("PRODUCT_BRAND")
	private String productBrand;

	@Column("PRODUCT_SN")
	private String productSn;

	@Column("PRODUCT_PRICE")
	private BigDecimal productPrice;

	@Column("PRODUCT_QUANTITY")
	private Integer productQuantity;


}
