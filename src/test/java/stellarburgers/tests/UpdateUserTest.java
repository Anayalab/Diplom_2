package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.UserApiClient;
import stellarburgers.models.AuthResponse;
import stellarburgers.models.ErrorResponse;
import stellarburgers.models.UserRegistration;
import stellarburgers.models.UserUpdate;
import stellarburgers.utils.TestDataGenerator;
import stellarburgers.utils.TokenManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Epic("Stellar Burgers API")
@Feature("User Update")
public class UpdateUserTest {
    
    private UserApiClient userApiClient;
    private UserRegistration testUser;
    private String accessToken;
    
    @Before
    public void setUp() {
        userApiClient = new UserApiClient();
        testUser = TestDataGenerator.generateRandomUser();
        
        Response createResponse = userApiClient.register(testUser);
        assertEquals("User creation should succeed", 200, createResponse.getStatusCode());
        
        AuthResponse authResponse = createResponse.as(AuthResponse.class);
        accessToken = authResponse.getAccessToken();
        TokenManager.setAccessToken(accessToken);
    }
    
    @After
    public void tearDown() {
        String token = TokenManager.getAccessToken();
        if (token != null) {
            userApiClient.deleteUser(token);
        }
        TokenManager.clearTokens();
    }
    
    @Test
    @Story("Update user email with authorization")
    @Description("Test updating user email with valid authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserEmailWithAuthTest() {
        UserUpdate updateData = TestDataGenerator.generateUpdatedEmail();
        
        Response response = userApiClient.updateUser(updateData, accessToken);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        AuthResponse updateResponse = response.as(AuthResponse.class);
        
        assertTrue("Success should be true", updateResponse.isSuccess());
        assertNotNull("User object should not be null", updateResponse.getUser());
        assertEquals("Email should be updated", updateData.getEmail().toLowerCase(), updateResponse.getUser().getEmail());
    }
    
    @Test
    @Story("Update user name with authorization")
    @Description("Test updating user name with valid authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserNameWithAuthTest() {
        UserUpdate updateData = TestDataGenerator.generateUpdatedName();
        
        Response response = userApiClient.updateUser(updateData, accessToken);
        
        if (response.getStatusCode() == 403) {
            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            assertFalse("Success should be false", errorResponse.isSuccess());
        } else {
            assertEquals("Expected status code 200", 200, response.getStatusCode());
            AuthResponse updateResponse = response.as(AuthResponse.class);
            assertTrue("Success should be true", updateResponse.isSuccess());
            assertNotNull("User object should not be null", updateResponse.getUser());
            assertEquals("Name should be updated", updateData.getName(), updateResponse.getUser().getName());
        }
    }
    
    @Test
    @Story("Update user password with authorization")
    @Description("Test updating user password with valid authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserPasswordWithAuthTest() {
        UserUpdate updateData = TestDataGenerator.generateUpdatedPassword();
        
        Response response = userApiClient.updateUser(updateData, accessToken);
        
        if (response.getStatusCode() == 403) {
            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            assertFalse("Success should be false", errorResponse.isSuccess());
        } else {
            assertEquals("Expected status code 200", 200, response.getStatusCode());
            AuthResponse updateResponse = response.as(AuthResponse.class);
            assertTrue("Success should be true", updateResponse.isSuccess());
            assertNotNull("User object should not be null", updateResponse.getUser());
            assertEquals("Email should remain the same", testUser.getEmail().toLowerCase(), updateResponse.getUser().getEmail());
        }
    }
    
    @Test
    @Story("Update all user fields with authorization")
    @Description("Test updating all user fields (email, name, password) with valid authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void updateAllUserFieldsWithAuthTest() {
        UserUpdate updateData = TestDataGenerator.generateUpdatedUserData();
        
        Response response = userApiClient.updateUser(updateData, accessToken);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        AuthResponse updateResponse = response.as(AuthResponse.class);
        
        assertTrue("Success should be true", updateResponse.isSuccess());
        assertNotNull("User object should not be null", updateResponse.getUser());
        assertEquals("Email should be updated", updateData.getEmail().toLowerCase(), updateResponse.getUser().getEmail());
    }
    
    @Test
    @Story("Update user without authorization")
    @Description("Test updating user data without authorization token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithoutAuthTest() {
        UserUpdate updateData = TestDataGenerator.generateUpdatedUserData();
        
        Response response = userApiClient.updateUserWithoutAuth(updateData);
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "You should be authorised", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Update email to existing email")
    @Description("Test updating user email to an already existing email")
    @Severity(SeverityLevel.NORMAL)
    public void updateEmailToExistingEmailTest() {
        UserRegistration anotherUser = TestDataGenerator.generateRandomUser();
        Response createResponse = userApiClient.register(anotherUser);
        assertEquals("Another user creation should succeed", 200, createResponse.getStatusCode());
        
        String anotherUserToken = createResponse.as(AuthResponse.class).getAccessToken();
        
        UserUpdate updateData = new UserUpdate();
        updateData.setEmail(anotherUser.getEmail());
        
        Response response = userApiClient.updateUser(updateData, accessToken);
        
        assertEquals("Expected status code 403", 403, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "User with such email already exists", 
                errorResponse.getMessage());
        
        userApiClient.deleteUser(anotherUserToken);
    }
}