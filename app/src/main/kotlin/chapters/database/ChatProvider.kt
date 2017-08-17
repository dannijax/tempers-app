package chapters.database

import chapters.extension.format_time
import chapters.extension.toFeeBackDate
import chapters.extension.toHeaderDate
import chapters.network.pojo.ChatPojo
import chapters.network.pojo.User
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*


class ChatProvider {

    companion object {
        fun saveNewMessage(list: ArrayList<ChatPojo>, seen: Boolean = false) {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            list.forEach {
                var chat = realm.where(Chat::class.java).equalTo("chatId", it.conversationalistID).findFirst()
                if (chat == null) {
                    chat = Chat()
                    chat.timeStamp = it.timeStamp
                    chat.name = it.conversationalistName
                    chat.chatId = it.conversationalistID
                    chat.time = it.date
                    chat.version = it.conversationalistVersion
                    chat.dateHeader = it.headerDate
                    val message = ChatMessage()
                    message.id = it.messageID
                    message.text = it.messageContent
                    message.time = it.time
                    message.chatId = it.conversationalistID
                    message.dateHeader = it.headerDate
                    message.timeStamp = it.timeStamp
                    chat.message.add(message)
                    message.seen = seen
                    chat.dateHeader = it.headerDate
                    realm.insertOrUpdate(chat)

                } else {
                    val message = ChatMessage()
                    message.id = it.messageID
                    message.chatId = it.conversationalistID
                    message.text = it.messageContent
                    message.time = it.time
                    message.seen = seen
                    message.dateHeader = it.headerDate
                    message.timeStamp = it.timeStamp
                    chat.time = it.date
                    chat.dateHeader = it.headerDate
                    chat.timeStamp = it.timeStamp
                    chat.dateHeader = it.headerDate
                    chat.message.add(message)
                }
            }
            realm.commitTransaction()
        }

        fun getChatList(): RealmResults<Chat> {
            val realm = Realm.getDefaultInstance()
            val list = realm.where(Chat::class.java).findAllSorted("timeStamp", Sort.DESCENDING)
            realm.beginTransaction()
            list.forEach { item1 ->
                item1.time = item1.timeStamp.toFeeBackDate()
                item1.message.forEach { item ->
                    item.dateHeader = item.timeStamp.toHeaderDate()
                    item.time = format_time.format(Date(item.timeStamp))
                }
            }
            realm.commitTransaction()
            return list
        }

        fun getCountUnreadChat(): Int {
            val realm = Realm.getDefaultInstance()
            val size = realm.where(Chat::class.java)
                    .findAll()
                    .toList()
                    .filter { it.countUnread > 0 }
                    .size
            realm.close()
            return size

        }

        fun getUserMessage(id: String?): RealmResults<ChatMessage> {
            val realm = Realm.getDefaultInstance()
            val list = realm.where(ChatMessage::class.java).equalTo("chatId", id)
                    .findAllSorted("timeStamp", Sort.ASCENDING)
            list.forEach { item ->
                realm.executeTransaction {
                    item.seen = true
                }
            }
            return list

        }

        fun saveNewMessage(message: ChatMessage, user: User?) {
            Realm.getDefaultInstance().use { realm->
                val ch = realm.where(Chat::class.java).equalTo("chatId", message.chatId).findFirst()
                if (ch != null) {
                    realm.executeTransaction {
                        ch.timeStamp=message.timeStamp
                        ch.time = message.time
                        ch.message.add(message)
                    }
                } else {
                    val chat = Chat()
                    chat.timeStamp = message.timeStamp
                    chat.name = user?.name ?: ""
                    chat.chatId = message.chatId
                    chat.time = message.time
                    chat.version = user?.version.toString()
                    chat.dateHeader = message.dateHeader
                    chat.message.add(message)
                    realm.beginTransaction()
                    realm.insertOrUpdate(chat)
                    realm.commitTransaction()
                }
            }


        }

        fun deleteChatWithUser(userId: String?) {
            Realm.getDefaultInstance().use {realm->
                val chat=realm.where(Chat::class.java).equalTo("chatId",userId).findAll()
                val message=realm.where(ChatMessage::class.java).equalTo("chatId",userId).findAll()
                realm.beginTransaction()
                message.deleteAllFromRealm()
                chat.deleteAllFromRealm()
                realm.commitTransaction()
            }

        }

    }
}