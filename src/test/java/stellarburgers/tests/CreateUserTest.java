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
import stellarburgers.utils.TestDataGenerator;
import stellarburgers.utils.TokenManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Epic("Stellar Burgers API")
@Feature("User Registration")
public class CreateUserTest {
    
    private UserApiClient userApiClient;
    private UserRegistration testUser;
    
    @Before
    public void setUp() {
        userApiClient = new UserApiClient();
        testUser = TestDataGenerator.generateRandomUser();
    }
    
    @After
    public void tearDown() {
        String accessToken = TokenManager.getAccessToken();
        if (accessToken != null) {
            userApiClient.deleteUser(accessToken);
        }
        TokenManager.clearTokens();
    }
    
    @Test
    @Story("Create unique user")
    @Description("Test creating a new user with all required fields")
    @Severity(SeverityLevel.BLOCKER)
    public void createUniqueUserWithAllFieldsTest() {
        Response response = userApiClient.register(testUser);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        AuthResponse authResponse = response.as(AuthResponse.class);
        
        assertTrue("Success should be true", authResponse.isSuccess());
        assertNotNull("Access token should not be null", authResponse.getAccessToken());
        assertNotNull("Refresh token should not be null", authResponse.getRefreshToken());
        assertNotNull("User object should not be null", authResponse.getUser());
        assertEquals("Email should match", testUser.getEmail().toLowerCase(), authResponse.getUser().getEmail());
        assertEquals("Name should match", testUser.getName(), authResponse.getUser().getName());
        
        TokenManager.setAccessToken(authResponse.getAccessToken());
        TokenManager.setRefreshToken(authResponse.getRefreshToken());
    }
    
    @Test
    @Story("Create duplicate user")
    @Description("Test creating a user that already exists")
    @Severity(SeverityLevel.CRITICAL)
    public void createDuplicateUserTest() {
        Response firstResponse = userApiClient.register(testUser);
        assertEquals("First user creation should succeed", 200, firstResponse.getStatusCode());
        TokenManager.setAccessToken(firstResponse.as(AuthResponse.class).getAccessToken());
        
        Response duplicateResponse = userApiClient.register(testUser);
        
        assertEquals("Expected status code 403", 403, duplicateResponse.getStatusCode());
        
        ErrorResponse errorResponse = duplicateResponse.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", "User already exists", errorResponse.getMessage());
    }
    
    @Test
    @Story("Create user without email")
    @Description("Test creating a user without email field")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserWithoutEmailTest() {
        testUser.setEmail(null);
        
        Response response = userApiClient.register(testUser);
        
        assertEquals("Expected status code 403", 403, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "Email, password and name are required fields", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Create user without password")
    @Description("Test creating a user without password field")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserWithoutPasswordTest() {
        testUser.setPassword(null);
        
        Response response = userApiClient.register(testUser);
        
        assertEquals("Expected status code 403", 403, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "Email, password and name are required fields", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Create user without name")
    @Description("Test creating a user without name field")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserWithoutNameTest() {
        testUser.setName(null);
        
        Response response = userApiClient.register(testUser);
        
        assertEquals("Expected status code 403", 403, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "Email, password and name are required fields", 
                errorResponse.getMessage());
    }
}