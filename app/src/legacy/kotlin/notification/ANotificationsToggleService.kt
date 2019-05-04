package notification

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import com.github.salomonbrys.kodein.instance
import core.Tunnel
import gs.environment.inject


class ANotificationsToggleService : IntentService("notificationsToggle") {
    private var mHandler: Handler = Handler()

    override fun onHandleIntent(intent: Intent) {
        val t: Tunnel = this.inject().instance()
        t.enabled %= intent.getBooleanExtra("new_state", true)
        mHandler.post(DisplayToastRunnable(this, "Hello World!"))
    }

}
