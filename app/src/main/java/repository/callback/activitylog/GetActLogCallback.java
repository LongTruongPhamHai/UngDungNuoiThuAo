package repository.callback.activitylog;

import model.ActivityLog;
import model.SportLog;

public interface GetActLogCallback {
    public void onSuccess(ActivityLog actLog);
    public void onFailure(Exception e);
}
