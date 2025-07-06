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
import stellarburgers.api.IngredientApiClient;
import stellarburgers.api.OrderApiClient;
import stellarburgers.api.UserApiClient;
import stellarburgers.models.AuthResponse;
import stellarburgers.models.ErrorResponse;
import stellarburgers.models.IngredientsResponse;
import stellarburgers.models.OrderRequest;
import stellarburgers.models.OrderResponse;
import stellarburgers.models.UserRegistration;
import stellarburgers.utils.TestDataGenerator;
import stellarburgers.utils.TokenManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Epic("Stellar Burgers API")
@Feature("Order Creation")
public class CreateOrderTest {
    
    private OrderApiClient orderApiClient;
    private UserApiClient userApiClient;
    private IngredientApiClient ingredientApiClient;
    private UserRegistration testUser;
    private String accessToken;
    private List<String> validIngredients;
    
    @Before
    public void setUp() {
        orderApiClient = new OrderApiClient();
        userApiClient = new UserApiClient();
        ingredientApiClient = new IngredientApiClient();
        
        Response ingredientsResponse = ingredientApiClient.getIngredients();
        assertEquals("Ingredients request should succeed", 200, ingredientsResponse.getStatusCode());
        
        IngredientsResponse ingredients = ingredientsResponse.as(IngredientsResponse.class);
        assertTrue("Should have ingredients", ingredients.getData().size() > 0);
        
        validIngredients = new ArrayList<>();
        int count = Math.min(3, ingredients.getData().size());
        for (int i = 0; i < count; i++) {
            validIngredients.add(ingredients.getData().get(i).get_id());
        }
        
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
    @Story("Create order with authorization")
    @Description("Test creating an order with valid ingredients and authorization")
    @Severity(SeverityLevel.BLOCKER)
    public void createOrderWithAuthTest() {
        OrderRequest orderRequest = new OrderRequest(validIngredients);
        
        Response response = orderApiClient.createOrder(orderRequest, accessToken);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        OrderResponse orderResponse = response.as(OrderResponse.class);
        
        assertTrue("Success should be true", orderResponse.isSuccess());
        assertNotNull("Order name should not be null", orderResponse.getName());
        assertNotNull("Order should not be null", orderResponse.getOrder());
        assertNotNull("Order number should not be null", orderResponse.getOrder().getNumber());
        assertTrue("Order number should be positive", orderResponse.getOrder().getNumber() > 0);
    }
    
    @Test
    @Story("Create order without authorization")
    @Description("Test creating an order without authorization token")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithoutAuthTest() {
        OrderRequest orderRequest = new OrderRequest(validIngredients);
        
        Response response = orderApiClient.createOrderWithoutAuth(orderRequest);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        OrderResponse orderResponse = response.as(OrderResponse.class);
        
        assertTrue("Success should be true", orderResponse.isSuccess());
        assertNotNull("Order name should not be null", orderResponse.getName());
        assertNotNull("Order should not be null", orderResponse.getOrder());
        assertNotNull("Order number should not be null", orderResponse.getOrder().getNumber());
        assertTrue("Order number should be positive", orderResponse.getOrder().getNumber() > 0);
    }
    
    @Test
    @Story("Create order with empty ingredients")
    @Description("Test creating an order with empty ingredients list")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithEmptyIngredientsTest() {
        OrderRequest orderRequest = new OrderRequest(new ArrayList<>());
        
        Response response = orderApiClient.createOrder(orderRequest, accessToken);
        
        assertEquals("Expected status code 400", 400, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "Ingredient ids must be provided", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Create order without ingredients")
    @Description("Test creating an order without ingredients field")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithoutIngredientsTest() {
        OrderRequest orderRequest = new OrderRequest();
        
        Response response = orderApiClient.createOrder(orderRequest, accessToken);
        
        assertEquals("Expected status code 400", 400, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "Ingredient ids must be provided", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Create order with invalid ingredients")
    @Description("Test creating an order with invalid ingredient IDs")
    @Severity(SeverityLevel.NORMAL)
    public void createOrderWithInvalidIngredientsTest() {
        List<String> invalidIngredients = Arrays.asList(TestDataGenerator.INVALID_INGREDIENT_HASH);
        
        OrderRequest orderRequest = new OrderRequest(invalidIngredients);
        
        Response response = orderApiClient.createOrder(orderRequest, accessToken);
        
        assertEquals("Expected status code 400", 400, response.getStatusCode());
    }
}