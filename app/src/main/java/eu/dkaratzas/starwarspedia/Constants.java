package eu.dkaratzas.starwarspedia;

public class Constants {
    public static final String BASE_URL = "https://api.graphcms.com/simple/v1/swapi";
    public static final String IN_APP_BILLING_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlY2hc+VE0bvUoUFuC1j1uk1rJmWVANE08lh0n210JjMAGm4NNyQlRZS6C9A6XCxADQ+LuFlMF+2CuNmg7nH//bCrdgPkfOXC+bSR4r16O6pRcZc/wHUpCTt2KNA4aG1oidP7Q5yClwpf4oHaVCtdWWCHw/1mGoKYP20koI5yPmMHbRmCIDAeJRy1KSjXxC5MM9eR7nAZdON455xCqR0fhlMdKPi1tNkzXiICqsJ+w+lEVCgmKyo7ZcgJoHU++zfVL6+laJVytdaLjI1hCxTbrebeH76HS5wByXduOcoGgxR/yFNYgYeHkzEemO187nKiJg1kgLttWEQNS3w7DEE2BwIDAQAB";
    public static final String MERCHANT_ID = "11855250090964741704";

    public static String PREMIUM_PRODUCT_ID() {
        if (BuildConfig.DEBUG)
            return "android.test.purchased";

        return "eu.dkaratzas.starwarspedia.no_ads";
    }

    public static String NATIVE_AD_ID() {
        if (BuildConfig.DEBUG)
            return "ca-app-pub-3940256099942544/6300978111";

        return "ca-app-pub-7937197470485695~8245374691";
    }
}
