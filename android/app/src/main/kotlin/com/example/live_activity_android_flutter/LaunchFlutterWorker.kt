import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.live_activity_android_flutter.MainActivity

class LaunchFlutterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Start the Flutter app here
        startFlutterApp(applicationContext)
        return Result.success()
    }

    private fun startFlutterApp(context: Context) {
        // Launch the Flutter app from background
        // For example, you can use an Intent to start a FlutterActivity
        // Replace MyFlutterActivity::class.java with the actual activity class
        Toast.makeText(applicationContext, "Open", Toast.LENGTH_SHORT).show()

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
