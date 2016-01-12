package kushtrimpacaj.facecomp.interactors;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Kushtrim on 12.01.2016.
 */
public class FaceApiInteractor {

    private FaceServiceClient faceServiceClient;

    @Inject
    public FaceApiInteractor(FaceServiceClient faceServiceClient) {
        this.faceServiceClient = faceServiceClient;
    }

    public void detectFaces(Bitmap bitmap, FaceDetectionCallback faceDetectionCallback) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        new FaceDetectionTask(faceDetectionCallback).execute(inputStream);
    }

    public void compareFaces(UUID firstFace, UUID secondFace, FaceComparisonCallback faceComparisonCallback) {
        new FaceComparisonTask(firstFace, secondFace, faceComparisonCallback).execute();
    }

    private class FaceComparisonTask extends AsyncTask<Void, String, VerifyResult> {
        private UUID firstFace;
        private UUID secondFace;

        private FaceComparisonCallback faceComparisonCallback;
        private boolean succeed = true;
        Exception exception;

        FaceComparisonTask(UUID firstFace, UUID secondFace, FaceComparisonCallback faceComparisonCallback) {
            this.firstFace = firstFace;
            this.secondFace = secondFace;
            this.faceComparisonCallback = faceComparisonCallback;
        }

        @Override
        protected VerifyResult doInBackground(Void... params) {
            try {
                return faceServiceClient.verify(firstFace, secondFace);
            } catch (Exception e) {
                succeed = false;
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(VerifyResult result) {
            if (succeed) {
                faceComparisonCallback.onFaceCompared(result.isIdentical, result.confidence);
            } else {
                faceComparisonCallback.onError(exception.getMessage());
            }
        }
    }

    private class FaceDetectionTask extends AsyncTask<InputStream, Void, Face[]> {
        private FaceDetectionCallback faceDetectionCallback;
        private boolean succeed = true;
        Exception exception;

        FaceDetectionTask(FaceDetectionCallback faceDetectionCallback) {
            this.faceDetectionCallback = faceDetectionCallback;
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            try {
                return faceServiceClient.detect(params[0], true, false, null);
            } catch (Exception e) {
                succeed = false;
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] result) {
            if (succeed) {
                faceDetectionCallback.onFaceDetected(result);
            } else {
                faceDetectionCallback.onError(exception.getMessage());
            }
        }
    }

    public interface FaceDetectionCallback {

        void onFaceDetected(Face[] faces);

        void onError(String message);

    }

    public interface FaceComparisonCallback {

        void onError(String message);

        void onFaceCompared(boolean isIdentical, double confidence);
    }
}
