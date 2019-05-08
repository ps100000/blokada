package flavor

import adblocker.ListWidgetProvider
import adblocker.SocialShareCount
import adblocker.TunnelDashCountDropped
import adblocker.TunnelDashHostsCount
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.*
import core.*
import filter.DashFilterBlacklist
import filter.DashFilterWhitelist
import notification.NotificationDashOn
import notification.displayNotification
import notification.hideNotification
import update.AboutDash
import update.UpdateDash

fun newFlavorModule(ctx: Context): Kodein.Module {
    return Kodein.Module {
        bind<List<Dash>>() with singleton {
            listOf(
                    UpdateDash(ctx).activate(true),
                    TunnelDashCountDropped(ctx).activate(true),
                    DashFilterBlacklist(ctx).activate(true),
                    DashFilterWhitelist(ctx).activate(true),
                    DashDns(lazy).activate(true),
                    NotificationDashOn(ctx).activate(true),
                    TunnelDashHostsCount(ctx).activate(true),
                    SettingsDash(ctx).activate(true),
                    SocialShareCount(ctx).activate(true),
                    PatronDash(lazy).activate(false),
                    PatronAboutDash(lazy).activate(false),
                    DonateDash(lazy).activate(false),
                    NewsDash(lazy).activate(false),
                    FeedbackDash(lazy).activate(false),
                    FaqDash(lazy).activate(false),
                    ChangelogDash(lazy).activate(false),
                    AboutDash(ctx).activate(false),
                    CreditsDash(lazy).activate(false),
                    CtaDash(lazy).activate(false),
                    ShareLogDash(lazy).activate(false)
            )
        }
        onReady {
            val s: Tunnel = instance()
            val ui: UiState = instance()

            // Show confirmation message to the user whenever notifications are enabled or disabled
            ui.notifications.doWhenChanged().then {
                if (ui.notifications()) {
                    ui.infoQueue %= ui.infoQueue() + Info(InfoType.NOTIFICATIONS_ENABLED)
                } else {
                    ui.infoQueue %= ui.infoQueue() + Info(InfoType.NOTIFICATIONS_DISABLED)
                }
            }

            // Display notifications for dropped
            s.tunnelRecentDropped.doOnUiWhenSet().then {
                if (s.tunnelRecentDropped().isEmpty()) hideNotification(ctx)
                else if (ui.notifications()) displayNotification(ctx, s.tunnelRecentDropped().last())
            }

            s.tunnelRecentDropped.doWhenChanged().then{
                updateListWidget(ctx)
            }
            s.enabled.doWhenChanged().then{
                updateListWidget(ctx)
            }
            updateListWidget(ctx)

            // Hide notification when disabled
            ui.notifications.doOnUiWhenSet().then {
                hideNotification(ctx)
            }

            // Initialize default values for properties that need it (async)
            s.tunnelDropCount {}
        }


    }
}

fun updateListWidget(ctx: Context){
    val updateIntent = Intent(ctx.applicationContext, ListWidgetProvider::class.java)
    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    val widgetManager = AppWidgetManager.getInstance(ctx)
    val ids = widgetManager.getAppWidgetIds(ComponentName(ctx, ListWidgetProvider::class.java))
    updateIntent .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    ctx.sendBroadcast(updateIntent)
}