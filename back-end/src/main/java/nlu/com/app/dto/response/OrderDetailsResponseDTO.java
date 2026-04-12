package nlu.com.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import nlu.com.app.dto.request.AddressDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailsResponseDTO {

    public TimeFor5StatusOrder timeFor5StatusOrder;
    String status;
    private Long orderId;
    private Double totalAmount;
    private String paymentMethodName;
    private String statusCode;
    private List<OrderItemDTO> items;
    private AddressDto shippingAddress;
    private CustomerDTO customer;
    private Double amountDecrease;

    @Data
    public static class CustomerDTO {
        private String user_id;
        private String username;
        private String email;
    }

    @Data
    public static class OrderItemDTO {
        private String img;
        private String bookTitle;
        private Double price;
        private Integer quantity;
        private Double discount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeFor5StatusOrder {
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime pendingConfirmationDate;
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime confirmedDate;
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime shippingDate;
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime deliveredDate;
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime cancelledDate;
    }
}

