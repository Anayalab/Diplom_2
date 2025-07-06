package stellarburgers.utils;

import org.apache.commons.lang3.RandomStringUtils;
import stellarburgers.models.UserCredentials;
import stellarburgers.models.UserRegistration;
import stellarburgers.models.UserUpdate;

public class TestDataGenerator {
    
    public static UserRegistration generateRandomUser() {
        return new UserRegistration(
                generateRandomEmail(),
                generateRandomPassword(),
                generateRandomName()
        );
    }
    
    public static UserCredentials toCredentials(UserRegistration user) {
        return new UserCredentials(
                user.getEmail(),
                user.getPassword()
        );
    }
    
    public static UserUpdate generateUpdatedUserData() {
        return new UserUpdate(
                generateRandomEmail(),
                generateRandomPassword(),
                generateRandomName()
        );
    }
    
    public static UserUpdate generateUpdatedEmail() {
        UserUpdate update = new UserUpdate();
        update.setEmail(generateRandomEmail());
        return update;
    }
    
    public static UserUpdate generateUpdatedName() {
        UserUpdate update = new UserUpdate();
        update.setName(generateRandomName());
        return update;
    }
    
    public static UserUpdate generateUpdatedPassword() {
        UserUpdate update = new UserUpdate();
        update.setPassword(generateRandomPassword());
        return update;
    }
    
    public static String generateRandomEmail() {
        return "random-" + RandomStringUtils.randomAlphanumeric(10) + "@test.com";
    }
    
    public static String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }
    
    public static String generateRandomName() {
        return "User " + RandomStringUtils.randomAlphabetic(8);
    }
    
    public static final String INVALID_INGREDIENT_HASH = "60d3b41abdacab0026a733c0";
}