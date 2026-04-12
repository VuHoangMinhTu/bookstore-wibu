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
public class BookSearchRequestDTO extends BaseSearchDTO {

    String context;
    long categoryId;
    Double minPrice;
    Double maxPrice;
}
