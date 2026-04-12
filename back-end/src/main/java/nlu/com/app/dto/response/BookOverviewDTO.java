package nlu.com.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookOverviewDTO {
    Long bookId;
    String thumbnail;
    Double price;
    Integer quantityInStock;
    String title;
    Double avgRate;
    Integer rvCounts;
    String genre;
}
