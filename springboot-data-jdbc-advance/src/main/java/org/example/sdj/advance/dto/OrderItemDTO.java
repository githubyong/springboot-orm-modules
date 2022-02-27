package org.example.sdj.advance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sdj.advance.entity.OrderInfo;
import org.example.sdj.advance.entity.OrderItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO extends OrderItem {

   private OrderInfo orderInfo;
}
