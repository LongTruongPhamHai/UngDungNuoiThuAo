package repository.callback.user;

public interface EmailUsernameCheckCallback {
    public void onResult(boolean isTaken
    );
    public void onError(Exception e);
}
