package repository.callback.pet;

public interface UpdatePetCallback {
    public void onSuccess();
    public void onFailure(Exception e);
    public void onIncorrectPassword();
}
