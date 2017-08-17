package chapters.network.pojo

import chapters.database.SharedSetting


class PhoneList(val phoneList:ArrayList<String> = arrayListOf(),val userID:Int=SharedSetting.getUserId())