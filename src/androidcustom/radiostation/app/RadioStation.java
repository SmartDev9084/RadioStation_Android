package androidcustom.radiostation.app;

import android.app.Application;
import androidcustom.radiostation.R;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

public class RadioStation extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(
			this,
			"hn42aQubuMYrWiaaPx0UHfR1O5hXBT2oJFg4rxIJ",
			"Gl53xxqMeOBZamOUWvVgbPVPt5DdyXHt6nXCMPSU"
		);
		/*
		Parse.initialize(
			this,
			"mlwg1eW4qbqtpiMzLnBxDo0Y2MVMc33ZuWLhq0Ph",
			"puR4f6tsJK0Sdp0hv5rbsrKpqFkp78TIottgqx1B"
		);

		Parse.initialize(
			this,
			"yM57HwSGas7hO1y7T1pc03IS8o6haiDcai0zJew4",
			"clesZfGUbZl9b6WOtVlAnsVugLHD4cswMDQ3tT2G"
		);
		*/
//	    ParseFacebookUtils.initialize(getString(R.string.FACEBOOK_APP_ID));		// FACEBOOK APP ID
//		ParseTwitterUtils.initialize("gqv8aCXVDTtbfyHOZyYgoeijn", "62cwFFFqQlXN9WjE2CkbOw6DwljcepMgDvxaSrQNI7NgrwIhfK");

	}
}
