package nlu.com.app.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import nlu.com.app.dto.AppResponse;
import nlu.com.app.dto.response.UserDetailsResponseDTO;
import nlu.com.app.service.IUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/admin/api/user")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCustomerController {
    IUserDetailsService userDetailsService;

    @GetMapping("/{userId}/details")
    public AppResponse<UserDetailsResponseDTO> getUserDetails(@PathVariable Long userId) {
        return AppResponse.<UserDetailsResponseDTO>builder()
                .result(userDetailsService.getUserDetails(userId))
                .build();
    }
}
