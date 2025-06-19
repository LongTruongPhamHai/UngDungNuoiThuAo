package repository.callback.pet;

public interface PetSetupCallback {
    void onSuccess();
    void onError(Exception e);
}
