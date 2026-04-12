package nlu.com.app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponseDTO {

    private String userId;
    private List<CartItemResponseDTO> items;
    private LocalDateTime lastModified;
}
