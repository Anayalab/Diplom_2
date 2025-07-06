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
import stellarburgers.models.Order;
import stellarburgers.models.OrderRequest;
import stellarburgers.models.OrdersListResponse;
import stellarburgers.models.UserRegistration;
import stellarburgers.utils.TestDataGenerator;
import stellarburgers.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Epic("Stellar Burgers API")
@Feature("Get Orders")
public class GetOrdersTest {
    
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
        
        OrderRequest orderRequest = new OrderRequest(validIngredients);
        
        Response orderResponse = orderApiClient.createOrder(orderRequest, accessToken);
        assertEquals("Order creation should succeed", 200, orderResponse.getStatusCode());
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
    @Story("Get user orders with authorization")
    @Description("Test getting user orders with valid authorization")
    @Severity(SeverityLevel.BLOCKER)
    public void getUserOrdersWithAuthTest() {
        Response response = orderApiClient.getUserOrders(accessToken);
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        OrdersListResponse ordersResponse = response.as(OrdersListResponse.class);
        
        assertTrue("Success should be true", ordersResponse.isSuccess());
        assertNotNull("Orders list should not be null", ordersResponse.getOrders());
        assertTrue("Should have at least one order", ordersResponse.getOrders().size() > 0);
        
        Order order = ordersResponse.getOrders().get(0);
        assertNotNull("Order ID should not be null", order.get_id());
        assertNotNull("Order number should not be null", order.getNumber());
        assertTrue("Order number should be positive", order.getNumber() > 0);
    }
    
    @Test
    @Story("Get user orders without authorization")
    @Description("Test getting user orders without authorization token")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserOrdersWithoutAuthTest() {
        Response response = orderApiClient.getUserOrdersWithoutAuth();
        
        assertEquals("Expected status code 401", 401, response.getStatusCode());
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertFalse("Success should be false", errorResponse.isSuccess());
        assertEquals("Error message should match", 
                "You should be authorised", 
                errorResponse.getMessage());
    }
    
    @Test
    @Story("Get all orders")
    @Description("Test getting all orders from the system")
    @Severity(SeverityLevel.NORMAL)
    public void getAllOrdersTest() {
        Response response = orderApiClient.getAllOrders();
        
        assertEquals("Expected status code 200", 200, response.getStatusCode());
        
        OrdersListResponse ordersResponse = response.as(OrdersListResponse.class);
        
        assertTrue("Success should be true", ordersResponse.isSuccess());
        assertNotNull("Orders list should not be null", ordersResponse.getOrders());
        assertTrue("Should have at least one order", ordersResponse.getOrders().size() > 0);
        assertTrue("Total should be positive", ordersResponse.getTotal() > 0);
        assertTrue("Total today should be non-negative", ordersResponse.getTotalToday() >= 0);
        
        Order order = ordersResponse.getOrders().get(0);
        assertNotNull("Order ID should not be null", order.get_id());
        assertNotNull("Order number should not be null", order.getNumber());
        assertTrue("Order number should be positive", order.getNumber() > 0);
        assertNotNull("Order status should not be null", order.getStatus());
        assertNotNull("Order created date should not be null", order.getCreatedAt());
        assertNotNull("Order updated date should not be null", order.getUpdatedAt());
    }
}