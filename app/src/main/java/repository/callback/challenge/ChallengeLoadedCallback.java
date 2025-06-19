package repository.callback.challenge;

import model.Challenge;

public interface ChallengeLoadedCallback {
    public void onSuccess(Challenge challenge);
    public void onError(Exception errorMessage);
}
