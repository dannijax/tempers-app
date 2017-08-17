package chapters.main.profile.mvp

import chapters.base.BaseInterface
import chapters.network.pojo.User


interface MvpFollowing:BaseInterface {

    fun setAdapter(list: ArrayList<User>)
}