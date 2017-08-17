package chapters.main.main.fragments.mvp

import chapters.base.BaseInterface
import chapters.network.pojo.PostItem


interface MvpPost :BaseInterface {

    fun emptyList(empty:Boolean)
    fun addNewPost(list:ArrayList<PostItem>)
    fun loadNewPostFromLoc(list:ArrayList<PostItem>)

}