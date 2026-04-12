package nlu.com.app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDTO {
    Long id;
    String name;
    List<CategoryResponseDTO> children;
}
