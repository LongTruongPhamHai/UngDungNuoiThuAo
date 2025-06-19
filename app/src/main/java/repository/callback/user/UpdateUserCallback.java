package repository.callback.user;

public interface UpdateUserCallback {
    public void onSuccess();
    public void onFailure(Exception e);
    public void onIncorrectPw();
//    public void onUsernameTaken();
//    public void onEmailTaken();
}
