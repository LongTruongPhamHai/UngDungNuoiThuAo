package repository.callback.studylog;

import java.util.Map;

public interface GetDayStLogCallback {
    public void onSuccess(Map<String, Object> sumDataList);
    public void onFailure(Exception e);
}
