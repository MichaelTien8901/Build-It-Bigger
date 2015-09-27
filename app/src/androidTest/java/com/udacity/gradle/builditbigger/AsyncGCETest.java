package com.udacity.gradle.builditbigger;

import android.test.AndroidTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael Tien on 2015/9/26.
 */
public class AsyncGCETest extends AndroidTestCase implements PostExecuteCallbacks {
    private String joke = null;
    private CountDownLatch latch = new CountDownLatch(1);

    public void testAsyncTaskForGCEJoke() {
        EndpointsAsyncTask task = new EndpointsAsyncTask();
        task.mCallback = this;
        task.mContext = getContext();
        task.execute();
        try {
            latch.await(10, TimeUnit.SECONDS); // wait 10 seconds

        } catch (Exception e) {
            fail( e.getMessage());
        }
        assertNotNull("Joke from GCE", joke);
    }

    @Override
    public void AsyncTaskPostCallbackResult(String result) {
        joke = result;
        latch.countDown(); // notify
    }

}
