package nlu.com.app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddBooksToCollectionRequestDTO {

    private List<Long> bookIds;
}
