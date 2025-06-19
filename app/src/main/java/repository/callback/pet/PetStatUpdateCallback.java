package repository.callback.pet;

public interface PetStatUpdateCallback {
    public void onSuccess();
    public void onFailure();
    public void onError(Exception e);
}
