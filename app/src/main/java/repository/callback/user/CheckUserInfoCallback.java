package repository.callback.user;

import model.User;

public interface CheckUserInfoCallback {
    public void onSuccess(String userId);
    public void onFailure(Exception e);
    public void onEmailNotFound();
    public void onUsernameNotFound();
}
