package nlu.com.app.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookCollectionRequestDTO {

    private String name;
    private String description;
    private Boolean isPublic;
    private List<Long> bookIds;
}
