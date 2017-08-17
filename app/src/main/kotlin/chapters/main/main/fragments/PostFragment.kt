package chapters.main.main.fragments

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import chapters.base.BaseFragment
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.*
import chapters.extension.binding.scrollEnd
import chapters.main.main.NewPostActivity
import chapters.main.main.ToroVideoMaxActivity
import chapters.main.main.fragments.mvp.MvpPost
import chapters.main.main.fragments.mvp.PostPresenter
import chapters.network.NetApiClient
import chapters.network.pojo.PostItem
import chapters.ui.adapter.PostAdapter
import chapters.utils.location.RxLocation
import com.google.android.gms.location.places.ui.PlacePicker
import com.tempry.R
import im.ene.toro.Toro
import kotlinx.android.synthetic.main.fragment_home.*
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class PostFragment : BaseFragment(), MvpPost, NetRequest {

    private var presenter: PostPresenter? = null
    private var first = true
    private var withDistance = false
    private var adapter: PostAdapter? = null
    private var menuItem: MenuItem? = null
    private var disntace: Boolean = false

    var range = 10
    override fun setLayoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun afterCreateView() {
        super.afterCreateView()
        setHasOptionsMenu(true)
        Toro.attach(activity)
        presenter = PostPresenter(this)
        initView()
        initRv()
//        makeRequest()

    }

    private fun initView() {
        swipeRefresh.setOnRefreshListener {
            adapter?.list?.clear()
            adapter?.notifyDataSetChanged()
            first = true
            makeRequest()
        }
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary))

        fabAddPost.setOnClickListener {
            NewPostActivity.launch(activity, presenter?.firstPost)
        }

        viewLoc.initClick({
            val builder = PlacePicker.IntentBuilder().build(activity)
            startActivityForResult(builder, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        }, {
            withDistance = true
            range = it
            adapter?.list?.clear()
            adapter?.notifyDataSetChanged()
            makeRequest()
        })

        RxLocation.observLocation().take(1)
                .flatMap { NetApiClient.getAddressFromLatLng(it) }
                .subscribe({
                    viewLoc.setCityName(it?.getCity())
                },{it.printStackTrace()})
    }

    private fun initRv() {
        rvPost.init(activity)
        rvPost.addDividerDecoration()
        adapter = PostAdapter(activity, function = {video,playbackState->
//            startActivityForResult<ToroVideoMaxActivity>(
//                    ToroVideoMaxActivity.REQUEST_VIDEO_MAXIMIZED,
//                    ToroVideoMaxActivity.EXTRA_VIDEO_URL to video,
//                    ToroVideoMaxActivity.EXTRA_PLAYBACK_STATE to playbackState
//            )
            ToroVideoMaxActivity.launch(activity,video,playbackState)
        })
        rvPost.adapter = adapter
        rvPost.scrollEnd()
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { makeRequest() }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_home, menu)
        menuItem = menu?.findItem(R.id.action_filter)?.setOnMenuItemClickListener {
            viewLoc.animateChangeHeight({
                withDistance = it
                disntace=it
                if (it) {
                    menuItem?.setIcon(R.drawable.ic_close)

                } else {
                    menuItem?.setIcon(R.drawable.ic_filter)
                    adapter?.list?.clear()
                    first = true
                    makeRequest()
                }
            })
            false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun addNewPost(list: ArrayList<PostItem>) {
        swipeRefresh.isRefreshing = false
        adapter?.list?.addAll(list)
        adapter?.notifyDataSetChanged()
    }


    override fun emptyList(empty: Boolean) {
        if (empty) llEmpty.show() else llEmpty.hide()
        
        swipeRefresh.isRefreshing = false
    }

    override fun loadNewPostFromLoc(list: ArrayList<PostItem>) {
        adapter?.list?.clear()
        adapter?.list = list
        adapter?.notifyDataSetChanged()
        swipeRefresh.isRefreshing = false
    }

    override fun makeRequest() {
        if (!withDistance) {
            if (first) {
                first = false
                presenter?.loadFirstPost()
            } else {
                presenter?.loadPaginationPost(adapter?.list?.lastOrNull()?.id)
            }
        } else {
            if (first) {
                first = false
                presenter?.loadPaginationPostWithDistance(adapter?.list?.lastOrNull()?.id, range)
            } else {
                presenter?.loadPaginationPostWithDistance(adapter?.list?.lastOrNull()?.id, range)
            }
        }
    }

    override fun onError(it: Throwable?) {
        swipeRefresh.isRefreshing = false
        NetworkUtils.netError(it, this, activity)
    }

    override fun onDestroy() {
        presenter?.subs?.unsubscribe()
        Toro.detach(activity)
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        Toro.register(rvPost)
        adapter?.list?.clear()
        if (!disntace) {
            withDistance = false
            first=true
            makeRequest()

        }
    }

    override fun onPause() {
        super.onPause()
        Toro.unregister(rvPost)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            data?.let {
                val place = PlacePicker.getPlace(activity, data)
                place?.let {
                    first = true
                    withDistance = true
                    disntace = true
                    viewLoc.setCityName(place.name)
                    presenter?.setLoc(place.latLng)
                    adapter?.list?.clear()
                    adapter?.notifyDataSetChanged()
                    makeRequest()
                }
            }
        }
    }

}