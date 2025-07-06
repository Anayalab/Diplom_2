package stellarburgers.utils;

public class ApiConstants {
    // Auth endpoints
    public static final String AUTH_REGISTER = "/auth/register";
    public static final String AUTH_LOGIN = "/auth/login";
    public static final String AUTH_LOGOUT = "/auth/logout";
    public static final String AUTH_USER = "/auth/user";
    
    // Order endpoints
    public static final String ORDERS = "/orders";
    public static final String ORDERS_ALL = "/orders/all";
    
    // Ingredient endpoints
    public static final String INGREDIENTS = "/ingredients";
    
    // Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
}