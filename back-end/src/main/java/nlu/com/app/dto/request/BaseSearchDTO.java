package nlu.com.app.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseSearchDTO {

    @Builder.Default
    int page = 0;
    @Builder.Default
    int size = 10;
    String sort;
}
