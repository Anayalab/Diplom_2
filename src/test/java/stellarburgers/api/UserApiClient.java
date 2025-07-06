package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.UserCredentials;
import stellarburgers.models.UserRegistration;
import stellarburgers.models.UserUpdate;
import stellarburgers.utils.ApiConstants;

public class UserApiClient extends BaseApiClient {
    
    @Step("Register new user")
    public Response register(UserRegistration user) {
        return getRequestSpec()
                .body(user)
                .when()
                .post(ApiConstants.AUTH_REGISTER)
                .then()
                .extract()
                .response();
    }
    
    @Step("Login user")
    public Response login(UserCredentials credentials) {
        return getRequestSpec()
                .body(credentials)
                .when()
                .post(ApiConstants.AUTH_LOGIN)
                .then()
                .extract()
                .response();
    }
    
    @Step("Get user info")
    public Response getUser(String accessToken) {
        return getAuthorizedRequestSpec(accessToken)
                .when()
                .get(ApiConstants.AUTH_USER)
                .then()
                .extract()
                .response();
    }
    
    @Step("Update user info")
    public Response updateUser(UserUpdate userData, String accessToken) {
        return getAuthorizedRequestSpec(accessToken)
                .body(userData)
                .when()
                .patch(ApiConstants.AUTH_USER)
                .then()
                .extract()
                .response();
    }
    
    @Step("Update user info without authorization")
    public Response updateUserWithoutAuth(UserUpdate userData) {
        return getRequestSpec()
                .body(userData)
                .when()
                .patch(ApiConstants.AUTH_USER)
                .then()
                .extract()
                .response();
    }
    
    @Step("Delete user")
    public Response deleteUser(String accessToken) {
        return getAuthorizedRequestSpec(accessToken)
                .when()
                .delete(ApiConstants.AUTH_USER)
                .then()
                .extract()
                .response();
    }
    
    @Step("Logout user")
    public Response logout(String refreshToken) {
        return getRequestSpec()
                .body("{\"token\": \"" + refreshToken + "\"}")
                .when()
                .post(ApiConstants.AUTH_LOGOUT)
                .then()
                .extract()
                .response();
    }
}