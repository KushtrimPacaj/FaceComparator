package kushtrimpacaj.facecomp;

import android.app.Application;

import kushtrimpacaj.facecomp.di.ApplicationComponent;
import kushtrimpacaj.facecomp.di.ApplicationModule;
import kushtrimpacaj.facecomp.di.DaggerApplicationComponent;

/**
 * Created by Kushtrim on 12.01.2016.
 */
public class FaceCompApp extends Application {


    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
