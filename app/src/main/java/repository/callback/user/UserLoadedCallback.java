package repository.callback.user;

import model.User;

public interface UserLoadedCallback {
    void onUserLoaded(User nUser);
    void onFailure(Exception e);
}
