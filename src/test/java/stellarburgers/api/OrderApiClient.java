package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.OrderRequest;
import stellarburgers.utils.ApiConstants;

public class OrderApiClient extends BaseApiClient {
    
    @Step("Create order with authorization")
    public Response createOrder(OrderRequest orderRequest, String accessToken) {
        return getAuthorizedRequestSpec(accessToken)
                .body(orderRequest)
                .when()
                .post(ApiConstants.ORDERS)
                .then()
                .extract()
                .response();
    }
    
    @Step("Create order without authorization")
    public Response createOrderWithoutAuth(OrderRequest orderRequest) {
        return getRequestSpec()
                .body(orderRequest)
                .when()
                .post(ApiConstants.ORDERS)
                .then()
                .extract()
                .response();
    }
    
    @Step("Get user orders")
    public Response getUserOrders(String accessToken) {
        return getAuthorizedRequestSpec(accessToken)
                .when()
                .get(ApiConstants.ORDERS)
                .then()
                .extract()
                .response();
    }
    
    @Step("Get user orders without authorization")
    public Response getUserOrdersWithoutAuth() {
        return getRequestSpec()
                .when()
                .get(ApiConstants.ORDERS)
                .then()
                .extract()
                .response();
    }
    
    @Step("Get all orders")
    public Response getAllOrders() {
        return getRequestSpec()
                .when()
                .get(ApiConstants.ORDERS_ALL)
                .then()
                .extract()
                .response();
    }
}