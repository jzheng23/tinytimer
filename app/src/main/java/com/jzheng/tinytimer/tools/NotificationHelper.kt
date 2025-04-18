package com.jzheng.tinytimer.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.os.CombinedVibration
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.jzheng.tinytimer.MainActivity
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.service.TimerService
import androidx.core.graphics.createBitmap

class NotificationHelper(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "default"

    private val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra("openSurvey", true)
    }

    private val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId, "Timer", NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(false)
//            setSound(null, null)
//            enableVibration(true)
//            enableLights(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(context, channelId).setContentTitle("Timer Service")
            .setContentText("Running in the foreground").setSmallIcon(createIcon(0))
            .setOngoing(true).setSilent(true).setColor(Color.White.toArgb())
            .setContentIntent(pendingIntent).build()
    }

    fun showNotification(
        minutes: Int = 0,
        soundEnabled: Boolean = false,
        vibrationEnabled: Boolean = false
    ) {
        if (!MyPermissionManager.checkNotificationPermission(context)) {
            MyPermissionManager.requestNotificationPermission(context)
        } else {
            val icon = createIcon(iconInt = minutes)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle("Timer")
                .setContentText("$minutes minutes since unlocked.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setColor(Color.White.toArgb())
                .setContentIntent(pendingIntent)

            if (soundEnabled) {
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            } else {
                builder.setSilent(true)
            }

            if (vibrationEnabled) {
                testVibration()
            }

            notificationManager.notify(1, builder.build())
        }
    }

    @Suppress("DEPRECATION")
    private fun testVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 100, 200, 300), -1)
            val combinedVibration = CombinedVibration.createParallel(effect)
            vibratorManager.vibrate(combinedVibration)
        } else {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(longArrayOf(0, 100, 200, 300), -1)
        }
    }

    fun showStaticNotification() {
        val notification =
            NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.timer)
                .setContentTitle("Timer").setContentText("Timer service running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true).setSilent(true)
                .setColor(Color.White.toArgb()).setContentIntent(pendingIntent).build()

        notificationManager.notify(1, notification)
    }

    fun showAnimation(minutesPassed: Int) {
        when (MyPreferenceManager.getInt(
            context, context.getString(R.string.selected_animation), 0
        )) {
            0 -> showBlinkingAnimation(
                minutesPassed
            )

            1 -> showCirclingAnimation(
                minutesPassed
            )

            else -> {
            }
        }
    }

    fun showCirclingAnimation(minutes: Int) {
        val handler = Handler(Looper.getMainLooper())
        val iconStates = (0..8).toList() // Animation stages
        var currentIconState = 0

        val runnable = object : Runnable {
            override fun run() {
                showNotificationMini(minutes, iconStates[currentIconState], 8)
                currentIconState++
                if (currentIconState < iconStates.size) {
                    handler.postDelayed(this, (1000 / 8).toLong()) // Show next stage
                } else {
                    handler.removeCallbacksAndMessages(null) // Stop the animation
                }
            }
        }

        handler.post(runnable) // Start the animation
    }

    fun showBlinkingAnimation(minutes: Int) {
        val handler = Handler(Looper.getMainLooper())
        var currentIconState = 0
        var blinkCounter = 0

        val runnable = object : Runnable {
            override fun run() {
                showNotificationMini(minutes, currentIconState, 1)
                currentIconState = 1 - currentIconState
                blinkCounter++
                if (blinkCounter < 6) {
                    handler.postDelayed(this, 200) // Show next stage
                } else {
                    handler.removeCallbacksAndMessages(null) // Stop the animation
                }
            }
        }
        handler.post(runnable) // Start the animation
    }

    private fun showNotificationMini(
        minutes: Int, animationStage: Int, phases: Int
    ) {


        val notification = NotificationCompat.Builder(context, channelId).setSmallIcon(
            createIcon(
                iconInt = minutes,
                animationStage = animationStage,
                totalAnimationStages = phases
            )
        ).setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true).setSilent(true)
            .setColor(Color.White.toArgb()).setContentIntent(pendingIntent).build()
        notificationManager.notify(1, notification)
    }

    fun clearNotification() {
        notificationManager.cancel(1)
        context.stopService(Intent(context, TimerService::class.java))
    }

    private fun createIcon(
        iconInt: Int, animationStage: Int = 8, totalAnimationStages: Int = 8
    ): IconCompat {

        val numberIcon = iconInt.coerceAtMost(99)
        val backgroundColor = Color.Transparent
        val textColor = Color.White


        val size = 192 // Set the size of the icon (in pixels)
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor.toArgb()
            style = Paint.Style.FILL
        }

        val framePaint = Paint().apply {
            isAntiAlias = true
            color = textColor.toArgb()
            style = Paint.Style.STROKE // Set to STROKE to draw only the outline
            strokeWidth = size * 0.05f // Adjust the thickness of the circle
        }

        val radius = size * 0.45f
        val circleX = size / 2f
        val circleY = size / 2f

        canvas.drawCircle(circleX, circleY, radius, backgroundPaint)


        val path = Path()
        val rect = RectF(size * 0.05f, size * 0.05f, size * 0.95f, size * 0.95f)
        val cornerRadius = size * MyPreferenceManager.getInt(
            context,
            context.getString(R.string.icon_corner_radius),
            60
        ) / 200f
//        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)

        path.moveTo(rect.centerX(), rect.top)
// Add the right half of the top edge and the right-top corner
        path.arcTo(
            RectF(
                rect.right - cornerRadius * 2, rect.top, rect.right, rect.top + cornerRadius * 2
            ), 270f, 90f
        )
// Add the right edge and bottom-right corner
        path.arcTo(
            RectF(
                rect.right - cornerRadius * 2,
                rect.bottom - cornerRadius * 2,
                rect.right,
                rect.bottom
            ), 0f, 90f
        )
// Add the bottom edge and bottom-left corner
        path.arcTo(
            RectF(
                rect.left, rect.bottom - cornerRadius * 2, rect.left + cornerRadius * 2, rect.bottom
            ), 90f, 90f
        )
// Add the left edge and top-left corner
        path.arcTo(
            RectF(
                rect.left, rect.top, rect.left + cornerRadius * 2, rect.top + cornerRadius * 2
            ), 180f, 90f
        )
// Close the path by adding the left half of the top edge
        path.lineTo(rect.centerX(), rect.top)

        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        val partialPathLength = (animationStage.toFloat() / totalAnimationStages) * pathLength

        val partialPath = Path()
        pathMeasure.getSegment(0f, partialPathLength, partialPath, true)

        canvas.drawPath(partialPath, framePaint)

        val textPaint = Paint().apply {
            isAntiAlias = true
            color = textColor.toArgb()
            textSize = if (numberIcon < 10) size * 0.75f else size * 0.65f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD) // Make the text bold
        }
        val textX = size / 2f
        val textY = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

        canvas.drawText(numberIcon.toString(), textX, textY, textPaint)

        return IconCompat.createWithBitmap(bitmap)
    }

}


fun isSystemInDarkTheme(context: Context): Boolean {
    return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}


fun showMessage(context: Context, minutes: Int = 30) {
    Toast.makeText(
        context, "Hey, you've used your phone for $minutes minutes!", Toast.LENGTH_LONG
    ).show()
}

