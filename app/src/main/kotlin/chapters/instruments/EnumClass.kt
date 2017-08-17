package chapters.instruments

import com.tempry.R

/**
 * Created by VoVa on 30.04.2017.
 */
enum class EnumClass(val id:String,val image:Int,text:String="") {

    FOLLOWER("1", R.drawable.ic_plus_green),
    STAR("2",R.drawable.ic_star_orange),
    RETEMP("3",R.drawable.ic_retemp),
    NEW_TAG("4", R.drawable.ic_tag),
    TAG_STAR("5", R.drawable.ic_tag),
    VANISH("6",R.drawable.ic_star_orange),
    JOIN("7",R.drawable.ic_plus_green),
    MESSAGE("8", R.drawable.ic_mess_plus),
    RECOMENDATION("9",R.drawable.ic_plus_green);

    companion object{

        fun fromId(id:String): EnumClass {
            return EnumClass.values().firstOrNull { it.id==id }?:FOLLOWER
        }
    }

}
