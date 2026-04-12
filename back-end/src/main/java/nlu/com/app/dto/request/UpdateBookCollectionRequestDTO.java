package nlu.com.app.dto.request;

import lombok.Data;

@Data
public class UpdateBookCollectionRequestDTO {
    private String name;
    private String description;
    private Boolean isPublic;
}
