package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.utils.ApiConstants;

public class IngredientApiClient extends BaseApiClient {
    
    @Step("Get all ingredients")
    public Response getIngredients() {
        return getRequestSpec()
                .when()
                .get(ApiConstants.INGREDIENTS)
                .then()
                .extract()
                .response();
    }
}