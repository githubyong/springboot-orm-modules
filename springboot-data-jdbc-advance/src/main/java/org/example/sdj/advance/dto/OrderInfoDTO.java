package org.example.sdj.advance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sdj.advance.entity.OrderInfo;
import org.example.sdj.advance.entity.OrderItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDTO extends OrderInfo {

    List<OrderItem> orderItems;
}
