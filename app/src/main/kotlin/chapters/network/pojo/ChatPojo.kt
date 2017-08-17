package chapters.network.pojo

/**
 * Created by VoVa on 30.04.2017.
 */

class ChatPojo {

    var messageID: String = ""
    var conversationalistID: String = ""
    var messageType: String = ""
    var messageContent: String = ""
    var date: String = ""
    var timeStamp = 0L
    var seen: String = ""
    var conversationalistName: String = ""
    var conversationalistVersion: String = ""
    var headerDate=""
    var time=""
    override fun toString(): String {
        return "ChatPojo(messageID='$messageID', conversationalistID='$conversationalistID', messageType='$messageType', messageContent='$messageContent', date='$date', timeStamp=$timeStamp, seen='$seen', conversationalistName='$conversationalistName', conversationalistVersion='$conversationalistVersion', headerDate='$headerDate')"
    }


}
