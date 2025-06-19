package repository.callback.sportlog;

import java.util.Map;

public interface GetDaySpLogCallback {
    public void onSuccess(Map<String, Object> sumDataList);
    public void onFailure(Exception e);
}
