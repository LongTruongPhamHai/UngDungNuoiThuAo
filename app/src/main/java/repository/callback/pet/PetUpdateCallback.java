package repository.callback.pet;

public interface PetUpdateCallback {
    public void onUpdateSuccess();
    public void onUpdateFailed();
    public void onError(String errorMessage);
    public void onIncorrectPw();
    public void onUserNotFound();
}
