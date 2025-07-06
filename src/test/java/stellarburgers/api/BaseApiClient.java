package stellarburgers.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import stellarburgers.config.TestConfig;
import stellarburgers.utils.ApiConstants;

import static io.restassured.RestAssured.given;

public class BaseApiClient {
    protected RequestSpecification baseSpec;
    
    public BaseApiClient() {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(TestConfig.BASE_URL)
                .setBasePath(TestConfig.BASE_PATH)
                .setContentType(ContentType.JSON)
                .addHeader(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.CONTENT_TYPE_JSON)
                .addFilter(new AllureRestAssured());
        
        if (TestConfig.ENABLE_LOGGING) {
            specBuilder.log(LogDetail.ALL);
        }
        
        baseSpec = specBuilder.build();
    }
    
    protected RequestSpecification getRequestSpec() {
        return given().spec(baseSpec);
    }
    
    protected RequestSpecification getAuthorizedRequestSpec(String accessToken) {
        return given()
                .spec(baseSpec)
                .header(ApiConstants.HEADER_AUTHORIZATION, accessToken);
    }
}