package stellarburgers.utils;

public class TokenManager {
    private static ThreadLocal<String> accessToken = new ThreadLocal<>();
    private static ThreadLocal<String> refreshToken = new ThreadLocal<>();
    
    public static void setAccessToken(String token) {
        accessToken.set(token);
    }
    
    public static String getAccessToken() {
        return accessToken.get();
    }
    
    public static void setRefreshToken(String token) {
        refreshToken.set(token);
    }

    public static void clearTokens() {
        accessToken.remove();
        refreshToken.remove();
    }
}