package ee.bcs.valitalgud.persistence.order;

import ee.bcs.valitalgud.controller.order.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(expression = "java(order.getCreatedAt().toString())", target = "createdAt")
    OrderResponseDto toOrderResponseDto(Order order);
}