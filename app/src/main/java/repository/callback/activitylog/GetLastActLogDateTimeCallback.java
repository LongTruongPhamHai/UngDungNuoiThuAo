package repository.callback.activitylog;

public interface GetLastActLogDateTimeCallback {
    public void onSuccess(String datetime);
    public void onFailure(Exception e);
}
