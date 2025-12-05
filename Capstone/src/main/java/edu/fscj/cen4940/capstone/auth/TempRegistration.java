package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.auth.AuthController.RegisterRequest;
import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.entity.User;

public record TempRegistration(RegisterRequest request, String code) {

    public UserDTO toUserDTO() {
        UserDTO tempUser = new UserDTO(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.dateOfBirth(),
                request.age(),
                request.weight(),
                request.weight(),
                request.goalWeight(),
                request.height(),
                request.password()
        );

        return tempUser;
    }
}
