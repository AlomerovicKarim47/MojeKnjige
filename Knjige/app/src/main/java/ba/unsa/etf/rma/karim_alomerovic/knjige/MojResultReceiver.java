package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by User on 12.5.2018..
 */

public class MojResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public MojResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver)
    {
        mReceiver = receiver;
    }

    public interface Receiver{
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (mReceiver != null)
        {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
