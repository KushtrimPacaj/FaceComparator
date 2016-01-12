package kushtrimpacaj.facecomp.di;

import android.app.Application;
import android.content.Context;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kushtrimpacaj.facecomp.R;

/**
 * Created by Kushtrim on 12.01.2016.
 */
@Module
@Singleton
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {

        this.application = application;
    }

    @Provides
    @Singleton
    FaceServiceClient provideFaceApiClient() {
        return new FaceServiceRestClient(application.getString(R.string.face_api_key));
    }

    @Provides
    Context provideApplicationContext() {
        return application.getApplicationContext();
    }

}
