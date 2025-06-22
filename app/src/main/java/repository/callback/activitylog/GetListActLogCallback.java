package repository.callback.activitylog;

import java.util.List;
import java.util.Map;

public interface GetListActLogCallback {
    public void onSuccess(List<Map<String, Object>> actLogList);
    public void onFailure(Exception e);
}
