package chapters.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by VoVa on 30.04.2017.
 */
open class ChatMessage : RealmObject() {

    @PrimaryKey
    var id = ""
    var chatId = ""
    var text = ""
    var time = ""
    var seen = false
    var isMy = false
    var isSend = false
    var dateHeader = ""
    var timeStamp = 0L
    override fun toString(): String {
        return "ChatMessage(id='$id', chatId='$chatId', text='$text', time='$time', seen=$seen, isMy=$isMy, isSend=$isSend, dateHeader='$dateHeader', timeStamp=$timeStamp)"
    }


}