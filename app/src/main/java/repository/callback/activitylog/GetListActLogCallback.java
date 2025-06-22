package repository.callback.activitylog;

import java.util.Map;

public interface GetListActLogCallback {
    public void onSuccess(Map<String, Object> sumDataList);
    public void onFailure(Exception e);
}
