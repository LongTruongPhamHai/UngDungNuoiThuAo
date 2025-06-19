package repository.callback.user;

public interface RegistrationCallback {
    void onEmailTaken();
    void onUsernameTaken();
    void onSuccess(String userId);
    void onError(Exception e);
}
