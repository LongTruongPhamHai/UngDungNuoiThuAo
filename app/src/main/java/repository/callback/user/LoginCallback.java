package repository.callback.user;

public interface LoginCallback {
    void onSuccess(String userId);
    void onIncorrectPassword();
    void onUsernameNotFound();
    void onFailure(Exception e);
}
