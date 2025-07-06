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
import stellarburgers.models.UserCredentials;
import stellarburgers.models.UserRegistration;
import stellarburgers.utils.TestDataGenerator;
import stellarburgers.utils.TokenManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Epic("Stellar Burgers API")
@Feature("User Login")
public class LoginUserTest {
    
    private UserApiClient userApiClient;
    private UserRegistration testUser;
    private UserCredentials validCredentials;
    
    @Before
    public void setUp() {
        userApiClient = new UserApiClient();
        testUser = TestDataGenerator.generateRandomUser();
        
        Response createResponse = userApiClient.register(testUser);
        assertEquals("User creation should succeed", 200, createResponse.getStatusCode());
        
        AuthResponse authResponse = createResponse.as(AuthResponse.class);
        TokenManager.setAccessToken(authResponse.getAccessToken());
        
        validCredentials = TestDataGenerator.toCredentials(testUser);
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
    @Story("Login with valid credentials")
    @Description("Test login with correct email and password")
    @Severity(SeverityLevel.BLOCKER)
    public void loginWithValidCredentialsTest() {
        Response response = userApiClient.login(validCredentials);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        AuthResponse authResponse = response.as(AuthResponse.class);
        
        assertTrue("Success should be true", authResponse.isSuccess());
        assertNotNull("Access token should not be null", authResponse.getAccessToken());
        assertNotNull("Refresh token should not be null", authResponse.getRefreshToken());
        assertNotNull("User object should not be null", authResponse.getUser());
        assertEquals("Email should match", testUser.getEmail().toLowerCase(), authResponse.getUser().getEmail());
        assertEquals("Name should match", testUser.getName(), authResponse.getUser().getName());
    }
    
    @Test
    @Story("Login with incorrect email")
    @Description("Test login with wrong email")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithIncorrectEmailTest() {
        UserCredentials wrongCredentials = new UserCredentials(TestDataGenerator.generateRandomEmail(), validCredentials.getPassword());
        
        Response response = userApiClient.login(wrongCredentials);
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "email or password are incorrect", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Login with incorrect password")
    @Description("Test login with wrong password")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithIncorrectPasswordTest() {
        UserCredentials wrongCredentials = new UserCredentials(validCredentials.getEmail(), TestDataGenerator.generateRandomPassword());
        
        Response response = userApiClient.login(wrongCredentials);
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "email or password are incorrect", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Login without email")
    @Description("Test login without email field")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithoutEmailTest() {
        UserCredentials incompleteCredentials = new UserCredentials(null, validCredentials.getPassword());
        
        Response response = userApiClient.login(incompleteCredentials);
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "email or password are incorrect", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Login without password")
    @Description("Test login without password field")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithoutPasswordTest() {
        UserCredentials incompleteCredentials = new UserCredentials(validCredentials.getEmail(), null);
        
        Response response = userApiClient.login(incompleteCredentials);
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "email or password are incorrect", 
                errorResponse.getMessage());
    }
}