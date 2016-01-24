package kushtrimpacaj.facecomp.di;

import javax.inject.Singleton;

import dagger.Component;
import kushtrimpacaj.facecomp.presenters.FaceComparisonPresenter;

/**
 * Created by Kushtrim on 12.01.2016.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    FaceComparisonPresenter faceComparisonPresenter();
}
