package repository.callback.user;

import java.util.List;

import model.User;

public interface LoadTopUserCallback {
    public void onSuccess(List<User> topUser, int userRank);
    public void onFailure(Exception exception);
}
