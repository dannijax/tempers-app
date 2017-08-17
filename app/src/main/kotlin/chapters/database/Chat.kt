package chapters.database

import chapters.extension.SMALL_PHOTO
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Chat : RealmObject() {

    @PrimaryKey
    var chatId = "0"
    var photo = ""
        get() = SMALL_PHOTO.format(chatId, version)
    var name = ""
    var version = "0"
    var time = ""
    var countUnread = 0
        get() = message.filter { !it.seen }.size
    var message: RealmList<ChatMessage> = RealmList()
    var timeStamp = 0L
    var dateHeader = ""
}