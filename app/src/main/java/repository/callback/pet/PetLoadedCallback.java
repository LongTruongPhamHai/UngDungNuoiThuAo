package repository.callback.pet;

import model.Pet;

public interface PetLoadedCallback {
    void onPetLoaded(Pet nPet);
    void onFailure(Exception errorMessage);
}
