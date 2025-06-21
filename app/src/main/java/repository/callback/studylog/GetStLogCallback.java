package repository.callback.studylog;

import model.SportLog;
import model.StudyLog;

public interface GetStLogCallback {
    public void onSuccess(StudyLog stLog);
    public void onFailure(Exception e);
}
