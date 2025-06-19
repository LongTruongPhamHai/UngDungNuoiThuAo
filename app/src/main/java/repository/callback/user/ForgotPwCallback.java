package repository.callback.user;

public interface ForgotPwCallback {
    public void onSuccess();
    public void onFailure(Exception e);
}
