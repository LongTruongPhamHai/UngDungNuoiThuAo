package repository.callback.sportlog;

import model.SportLog;

public interface GetSpLogCallback {
    public void onSuccess(SportLog spLog);
    public void onFailure(Exception e);
}
