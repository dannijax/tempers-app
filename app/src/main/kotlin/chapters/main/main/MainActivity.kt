package chapters.main.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import android.view.inputmethod.InputMethodManager
import chapters.base.BaseActivity
import chapters.database.ChatProvider
import chapters.database.UserProvider
import chapters.extension.binding.RxBottomBar
import chapters.extension.requestPermisionsGps
import chapters.instruments.EnumMainNavigation
import chapters.main.SettingsActivity
import chapters.main.main.fragments.*
import chapters.main.reg.MainLoginActivity
import chapters.network.NetApiClient
import chapters.ui.adapter.VpAdapter
import chapters.ui.utils.GlideLoadCircle
import chapters.utils.location.RxLocation
import com.jakewharton.rxbinding.support.design.widget.RxNavigationView
import com.onesignal.OneSignal
import com.tempry.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import rx.subscriptions.CompositeSubscription


class MainActivity : BaseActivity() {

    private var comSub: CompositeSubscription? = null

    override fun setLayoutRes(): Int {
        return R.layout.activity_main
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        requestPermisionsGps()
        listenerChatDB()
        registerOneSignal()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View?) {
            }

            override fun onDrawerOpened(drawerView: View?) {
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }

        })
        toggle.syncState()

        val list: ArrayList<Fragment> = arrayListOf(PostFragment(), ProfileFragment(), NotificationsFragment(),
                SearchFragment(), InboxFragment())
        vpMain.adapter = VpAdapter(supportFragmentManager, list)
        vpMain.offscreenPageLimit = 4
        val subNv = RxNavigationView.itemSelections(nav_view)
                .subscribe {
                    val pos = EnumMainNavigation.fromId(it.itemId)
                    if (pos == 6) {
                        SettingsActivity.launch(this)
                        drawer_layout.closeDrawers()
                    } else if (pos == 5) {
                        MainLoginActivity.launch(this)
                        finish()
                    } else {
                        vpMain.currentItem = pos
                        bottomBar.selectTabAtPosition(pos)
                        drawer_layout.closeDrawers()
                    }
                }

        val subBot = RxBottomBar.onTabSelected(bottomBar)
                .subscribe {
                    if (it == 1) {
                        val fr = list.getOrNull(1) as ProfileFragment?
                        fr?.makeRequest()
                    }
                    vpMain.currentItem = it
                    nav_view.setCheckedItem(EnumMainNavigation.navIdFromPos(it))
                }

        comSub = CompositeSubscription(subNv, subBot)

    }

    private fun listenerChatDB() {
        val tab = bottomBar.getTabAtPosition(4)
        Realm.getDefaultInstance().addChangeListener {
            val count = ChatProvider.getCountUnreadChat()
            tab.setBadgeCount(count)
        }
    }

    private fun initUser() {
        val user = UserProvider.getMyProfile()
        nav_view.getHeaderView(0).tvName.text = user?.fullName
        nav_view.getHeaderView(0).tvEmail.text = user?.email
        GlideLoadCircle(nav_view.getHeaderView(0).ivPhoto, user?.photo)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermisionsGps()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else super.onBackPressed()
    }

    private fun registerOneSignal() {
        OneSignal.idsAvailable { userId, registrationId ->
            NetApiClient.registerOneSignal(userId)
                    .subscribe({}, { it.printStackTrace() })
        }
    }

    override fun onResume() {
        super.onResume()
        initUser()
        nav_view.setCheckedItem(EnumMainNavigation.navIdFromPos(vpMain.currentItem))
    }

    override fun finish() {
        RxLocation.disable()
        comSub?.unsubscribe()
        Realm.getDefaultInstance().removeAllChangeListeners()
        super.finish()
    }

}
