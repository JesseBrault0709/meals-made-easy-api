package app.mealsmadeeasy.api.auth;

public enum LoginExceptionReason {
    INVALID_CREDENTIALS,
    EXPIRED_REFRESH_TOKEN,
    INVALID_REFRESH_TOKEN,
    NO_REFRESH_TOKEN
}
