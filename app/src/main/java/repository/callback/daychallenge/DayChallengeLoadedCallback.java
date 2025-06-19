package repository.callback.daychallenge;

import java.util.List;

public interface DayChallengeLoadedCallback {
    public void onSuccess(List<String> challengeIdList);

    public void onFailure(Exception errorMessage);
}
