package repository.callback.activitylog;

import model.ActivityLog;

public interface GetActLogCallback {
    public void onSuccess(ActivityLog actLog);
    public void onFailure(Exception e);
}
