fun main() {

    var chatService = ChatService(mutableListOf(), mutableListOf())
    var text4CurrentUser1 = ""
    var text4CurrentUser2 = ""
    var myCurrentUser = ""
    var myCurrentUserId = 0;

    while (true) {
        if (text4CurrentUser1 != "") {
            text4CurrentUser2 = "" +
                    "4 - Список чатов (пользователей, с кем установлен чат)\n" +
                    "5 - Отправить сообщение пользователю\n" +
                    "6 - Удалить сообщение\n" +
                    "7 - Редактировать сообщение\n"
        }
        println(
            "===================================\n" +
                    "Главное меню ${text4CurrentUser1}" + "\n" +
                    "Выберете вариант:\n" +
                    "1 - посмотреть список пользователей\n" +
                    "2 - добавить нового пользователя\n" +
                    "3 - выбрать (сменить) пользователя для входа и доступа к чатам\n" +
                    "${text4CurrentUser2}" +
                    "0 - выход из программы"
        )
        var myChoice = readLine()
        when (myChoice) {
            "1" -> {
                println(
                    "===================================\n" +
                            "Список пользователей (${chatService.users.size})"
                )
                chatService.users.forEach { user ->
                    println("${user.id}. " + user.name)
                }

            }

            "2" -> {
                while (true) {
                    println(
                        "===================================\n" +
                                "Ввод нового пользователя\n" +
                                "Введите имя нового пользователя и нажмите Enter\n" +
                                "(если передумали заводить нового пользователя - введите 0 для возврата в главное меню):\n"
                    )
                    var newUserName = readLine()
                    if (newUserName.toString() == "0") {
                        break
                    } else if (chatService.addUser(newUserName.toString())) {
                        println("Пользователь ${newUserName} успешно добавлен")
                        break
                    } else {
                        println("Пользователь ${newUserName} уже существует")
                    }
                }
            }

            "3" -> {
                while (true) {
                    if (chatService.users.size == 0) {
                        println(
                            "===================================\n" +
                                    "Пока не введено ни одного пользователя\n" +
                                    "Сначала надо завести пользователей (хотя бы одного)"
                        )
                        break
                    }
                    println(
                        "===================================\n" +
                                "Выбор пользователя для входа в чат\n" +
                                "Введите номер, соответствующий пользователю из списка\n" +
                                "(для возврата в главное меню введите 0)"
                    )
                    println("Список пользователей (${chatService.users.size})")
                    chatService.users.forEach { user ->
                        println("${user.id}. " + user.name)
                    }
                    var numUser = readLine()?.toIntOrNull()
                    if (numUser == null) {
                        numUser = -10
                    }
                    if (numUser == 0) {
                        break
                    }
                    if (chatService.checkUserId(numUser.toInt())) {
                        for (eachUser in chatService.users) {
                            if (eachUser.id == numUser.toInt()) {
                                myCurrentUser = eachUser.name
                                myCurrentUserId = eachUser.id
                            }
                        }
                        if (myCurrentUser != "") {
                            text4CurrentUser1 = "(Сейчас Вы вошли как пользователь \"${myCurrentUser}\")"
                            println("Вы успешно зашли под пользователем \"${myCurrentUser}\"")
                            break
                        }
                    }
                }

            }

            "4" -> {
                if (myCurrentUser != "") {
                    if (chatService.messages.find { (it.userFromId == myCurrentUserId) or (it.userToId == myCurrentUserId) } != null) {

                        while (true) {
                            var listId = mutableListOf<Int>()
                            listId.clear()
                            println("У текущего пользователя \"${myCurrentUser}\" есть чаты с этими пользователями: ")
                            chatService.chatListByUserID(myCurrentUserId).forEach() { user ->
                                println(
                                    "${user?.id}. " + user?.name + " (Не прочитано сообщений всего:входящих:исходящих / Общее кол-во сообщений всего:входящих:исходящих = ${
                                        chatService.countOfReadMessagesByUsersId(
                                            myCurrentUserId,
                                            user!!.id
                                        )
                                    }/${chatService.countOfMessagesByUsersId(myCurrentUserId, user.id)})"
                                )
                                listId.add(user.id)
                            }
                            println(
                                "Выберете вариант:\n" +
                                        " - Чтобы войти в чат и прочитать сообщения чата = введите номер пользователя из списка выше\n" +
                                        " - Чтобы удалить чат = введите номер пользователя из списка выше со знаком минус\n" +
                                        " - Для возврата в главное меню введите 0"
                            )
                            var chatChoice = readLine()?.toIntOrNull()
                            if (chatChoice == 0) {
                                break
                            }
                            if (chatChoice == null) {
                                chatChoice = 0
                            }

                            if ((chatChoice > 0) and (listId.contains(chatChoice))) {
                                // выводим меседжи по ИД юзверей
                                println("Сколько выводить последних сообщений из чата?")
                                println("Введите число сообщений или 0 для вывода всех сообщений")
                                var messagesLimit = readLine()?.toIntOrNull()
                                if (messagesLimit == null) {
                                    messagesLimit = 0
                                }
                                if (messagesLimit < 0) {
                                    messagesLimit = 0
                                }

                                println("Ваш чат с пользователем \"${chatService.getUserNameById(chatChoice)}\" (Вы сейчас пользователь: \"${myCurrentUser}\")")
                                chatService.messageListByUsersId(myCurrentUserId, chatChoice, messagesLimit)
                                    .forEach() { mess ->
                                        var messageType =
                                            if (mess!!.userFromId == myCurrentUserId) "исходящее" else "входящее"
                                        println("--------------------------------")
                                        println("ID сообщения = ${mess!!.id}")
                                        println("Тип сообщения = ${messageType}")
                                        println("Сообщение:\n ${mess.text}")
                                        println("--------------------------------")
                                        if (mess!!.userToId == myCurrentUserId) {                               // отмечаем вх сообщение "прочитанным"
                                            // chatService.setMessageReadByMessageId(mess.id)
                                            chatService.messages.setMessageReadByMessageId_ext(mess.id)
                                        }
                                    }
                                println("Жми Enter для продолжения")
                                readln()
                            }
                            if ((chatChoice < 0) and (listId.contains(0 - chatChoice))) {
                                // удаляем меседжи по ИД юзверей
                                chatService.killMessagesByUsersId(myCurrentUserId, 0 - chatChoice)
                                println("Ваш чат с пользователем \"${chatService.getUserNameById(0 - chatChoice)}\" удален. (Вы сейчас пользователь: \"${myCurrentUser}\")")
                                println("Жми Enter для продолжения")
                                readln()
                            }
                            if (chatService.messages.find { (it.userFromId == myCurrentUserId) or (it.userToId == myCurrentUserId) } == null) {
                                break
                            }
                        }

                    } else {
                        println("Пользователь ${myCurrentUser}, под которым вы зашли, не участвует ни в одном чате")
                        println("Для продолжения нажмите Enter")
                        readln()
                    }
                }
            }

            "5" -> {
                if (myCurrentUser != "") {
                    while (true) {
                        println(
                            "===================================\n" +
                                    "Выберете пользователя, кому хотите отправить сообщение (самому себе отправлять нельзя)\n" +
                                    "Введите номер, соответствующий пользователю из списка\n" +
                                    "(для возврата в главное меню введите 0)"
                        )
                        println("Список доступных пользователей (${chatService.users.size - 1})")
                        chatService.users.forEach { user ->
                            if (user.id != myCurrentUserId) {
                                println("${user.id}. " + user.name)
                            }
                        }
                        var numUser = readLine()?.toIntOrNull()
                        if (numUser == null) {
                            numUser = -10
                        }
                        if (numUser == myCurrentUserId) {
                            numUser = -10
                        }
                        if (numUser == 0) {
                            break
                        }
                        if (chatService.checkUserId(numUser.toInt())) {
                            var toUserId = numUser.toInt()
                            var toUser = chatService.getUserNameById(toUserId)
                            println(
                                "===================================\n" +
                                        "Введите сообщение для пользователя ${toUser} и нажмите Enter:"
                            )
                            var message2Send = readLine().toString()
                            chatService.sendMessage(
                                Message(
                                    chatService.getMaxMessageId() + 1,
                                    myCurrentUserId,
                                    toUserId,
                                    message2Send,
                                    false
                                )
                            )
                            println("Сообщение отправлено")
                            break
                        }
                    }

                }
            }

            "6" -> {
                if (myCurrentUser != "") {
                    println("Удалять можно только сообщения, где текущий пользователь является либо получателем, либо отправителем")
                    println("Для продолжения нажмите Enter")
                    readln()
                    var listId = mutableListOf<Int>()
                    listId.clear()
                    chatService.allMessagesByUserId(myCurrentUserId).forEach() { mess ->
                        var messageType = if (mess!!.userFromId == myCurrentUserId) "исходящее" else "входящее"
                        println("--------------------------------")
                        println("ID сообщения = ${mess!!.id}")
                        println("Тип сообщения = ${messageType}")
                        println("От кого = ${chatService.getUserNameById(mess.userFromId)}")
                        println("Кому = ${chatService.getUserNameById(mess.userToId)}")
                        println("Сообщение:\n ${mess.text}")
                        println("--------------------------------")
                        listId.add(mess.id)
                    }
                    println("Выведены все сообщения, где текущий пользователь \"${myCurrentUser}\"является отправителем или получателем")
                    println("Для удаления выбранного сообщения введите его ID и нажмите Enter")
                    println("Если передумали - введите 0")
                    while (true) {
                        var killMessageId = readln().toIntOrNull()
                        if (killMessageId == null) {
                            killMessageId = -10
                        }
                        if (killMessageId == 0) {
                            break
                        }
                        if (killMessageId > 0 && listId.contains(killMessageId)) {
                            chatService.killMessageByMessageId(killMessageId)
                            println("сообщение удалено")
                            println("для продолжения нажмите Enter")
                            readln()
                            break
                        }
                        println("некорректный ввод, повторите пож-та")
                    }
                }
            }

            "7" -> {
                if (myCurrentUser != "") {
                    println("Отредактировать можно только сообщения, где текущий пользователь является отправителем")
                    println("Для продолжения нажмите Enter")
                    readln()
                    var listId = mutableListOf<Int>()
                    listId.clear()
                    chatService.allMessagesByUserId(myCurrentUserId).forEach() { mess ->
                        if (mess!!.userFromId == myCurrentUserId) {
                            println("--------------------------------")
                            println("ID сообщения = ${mess!!.id}")
                            println("Тип сообщения = Исходящее")
                            println("От кого = ${chatService.getUserNameById(mess.userFromId)}")
                            println("Кому = ${chatService.getUserNameById(mess.userToId)}")
                            println("Сообщение:\n ${mess.text}")
                            println("--------------------------------")
                            listId.add(mess.id)
                        }

                    }
                    println("Выведены все сообщения, где текущий пользователь \"${myCurrentUser}\"является отправителем")
                    println("Для редактирования выбранного сообщения введите его ID и нажмите Enter")
                    println("Если передумали - введите 0")
                    while (true) {
                        var editMessageId = readln().toIntOrNull()
                        if (editMessageId == null) {
                            editMessageId = -10
                        }
                        if (editMessageId == 0) {
                            break
                        }
                        if (editMessageId > 0 && listId.contains(editMessageId)) {
                            println("Введите текст нового сообщения:")
                            var editMessageText = readLine().toString()
                            // chatService.editMessageByMessageId(editMessageId, editMessageText)
                            chatService.messages.editMessageByMessageId_ext(editMessageId, editMessageText)
                            println("сообщение отредактировано")
                            println("для продолжения нажмите Enter")
                            readln()
                            break
                        }
                        println("некорректный ввод, повторите пож-та")
                    }
                }
            }

            "0" -> {
                println("До свидания!")
                break
            }
        }
    }

}

// ======================================== User Service

class ChatService(
    var users: MutableList<Users>,
    var messages: MutableList<Message>
) {
    fun addUser(name: String): Boolean {       // добавление нового юзера
        if (!checkUserName(name)) {
            users.add(Users(getMaxUsersId() + 1, name))
            return true
        }
        return false
    }

//    fun checkUserName(name: String): Boolean {     //проверяем, а есть ли юзер с таким именем?
//        if (users.find { it.name == name } != null) {
//            return true
//        } else {
//            return false
//        }
//    }

    fun checkUserName(name: String): Boolean {     //проверяем, а есть ли юзер с таким именем? ("улучшайзинг" функции)
        return users.find { it.name == name } != null
    }

//    fun checkUserId(id: Int): Boolean {     //проверяем, а есть ли такой юзер с таким id?
//        if (users.find { it.id == id } != null) {
//            return true
//        } else {
//            return false
//        }
//    }

    fun checkUserId(id: Int): Boolean {     //проверяем, а есть ли такой юзер с таким id? ("улучшайзинг" функции)
        return users.find { it.id == id } != null
    }

//    fun getMaxUsersId(): Int {                  //Получаем максимальный ID пользователя ==> переделали с требованием ДЗ по теме Sequences
//        var maxId = 0;
//        for (user in users) {
//            if (user.id > maxId) {
//                maxId = user.id
//            }
//        }
//        return maxId
//    }

    fun getMaxUsersId(): Int {                  //Получаем максимальный ID пользователя с учетом требований ДЗ по теме Sequences
        return users.maxOfOrNull { it.id } ?: 0
    }

//    fun getUserNameById(id: Int): String {              // Получаем имя пользователя по id пользователя ==> переделали с использованием lambda-функции
//        var result = ""
//        users.forEach { user ->
//            if (user.id==id) {
//                result = user.name
//            }
//        }
//        return result
//    }

    fun getUserNameById(id: Int): String {              // Получаем имя пользователя по id пользователя, используем lambda-функции
        val result = users.filter { it.id == id }
        return result.first().name
    }

//    fun getUserById(id: Int): Users? {                   // Получаем пользователя по id пользователя ==> переделали с использованием lambda-функции
//        var result: Users? = null
//        users.forEach { user ->
//            if (user.id == id) {
//                result = user
//            }
//        }
//        return result
//    }

    fun getUserById(id: Int): Users? {                   // Получаем пользователя по id пользователя с использованием lambda-функции
        val result = users.filter { it.id == id }
        return result.first()
    }

    fun sendMessage(message: Message) {
        messages.add(message)
    }

//    fun getMaxMessageId(): Int {                //Получаем максимальный ID сообщения ==> переделали с требованием ДЗ по теме Sequences
//        var maxId = 0;
//        for (mess in messages) {
//            if (mess.id > maxId) {
//                maxId = mess.id
//            }
//        }
//        return maxId
//    }

    fun getMaxMessageId(): Int {                //Получаем максимальный ID сообщения с учетом требований ДЗ по теме Sequences
        return messages.maxOfOrNull { it.id } ?: 0
    }

//    fun setMessageReadByMessageId (id: Int) {      // устанавливаем сообщения "прочитанными"  ==> переделали через Extension Functions
//        for ((index, mess) in messages.withIndex()) {
//            if (mess.id == id) {
//                messages[index] = mess.copy(readIt = true)
//            }
//        }
//    }


//    fun editMessageByMessageId (id: Int, text: String) {          // редактируем сообщение  ==> переделали через Extension Functions
//        for ((index, mess) in messages.withIndex()) {
//            if (mess.id == id) {
//                messages[index] = mess.copy(text = text)
//            }
//        }
//    }

//    fun chatListByUserID(id: Int): List<Users?> {        // получаем список "контактов" по ID юзера ==> переделали с требованием ДЗ по теме Sequences
//        var list = mutableListOf<Users?>()
//        messages.forEach { mess ->
//            if ((mess.userFromId == id)) {
//                list.add(getUserById(mess.userToId))
//            }
//            if ((mess.userToId == id)) {
//                list.add(getUserById(mess.userFromId))
//            }
//        }
//        return list.distinct()
//    }

    fun chatListByUserID(id: Int): List<Users?> {        // получаем список "контактов" по ID юзера с учетом требований ДЗ по теме Sequences

        var list = mutableListOf<Users?>()

        messages.filter { it.userFromId == id }
                .map { it.userToId }
                .forEach { list.add(getUserById(it)) }

        messages.filter { it.userToId == id }
                .map { it.userFromId }
                .forEach { list.add(getUserById(it)) }

        return list.distinct()
    }

    fun messageListByUsersId(
        myUserId: Int,
        partnerUserId: Int,
        messagesLimit: Int
    ): List<Message?> {   // выводим сообщения одного чата (для 2-х пользователей)
        var list = mutableListOf<Message?>()
        messages.forEach { mess ->
            if (mess.userFromId == myUserId && mess.userToId == partnerUserId) {
                list.add(mess)
            }
            if (mess.userFromId == partnerUserId && mess.userToId == myUserId) {
                list.add(mess)
            }
        }
        list.sortBy { it?.id }

        while (messagesLimit > 0 && list.size > messagesLimit) {            // ограничиваем вывод количеством заданных сообщений, если 0 - то не ограничиваем
            list.removeAt(0)
        }

        return list
    }

//    fun allMessagesByUserId(myUserId: Int): List<Message?> {   // выводим все сообщения одного пользователя (отправленные ему или от него) ==> переделали с требованием ДЗ по теме Sequences
//        var list = mutableListOf<Message?>()
//        messages.forEach { mess ->
//            if (mess.userFromId == myUserId || mess.userToId == myUserId) {
//                list.add(mess)
//            }
//        }
//        list.sortBy { it?.id }
//        return list
//    }

    fun allMessagesByUserId(myUserId: Int): List<Message?> {   // выводим все сообщения одного пользователя (отправленные ему или от него) с учетом требований ДЗ по теме Sequences
        var list = messages.filter { it.userFromId == myUserId || it.userToId == myUserId }
                           .sortedBy { it.id }

        return list
    }

    fun countOfMessagesByUsersId(
        myUserId: Int,
        partnerUserId: Int
    ): String {         // посчитаем, сколько сообщений в чате
        var inputMessages = 0
        var outpuMessages = 0
        messages.forEach { mess ->
            if (mess.userFromId == myUserId && mess.userToId == partnerUserId) {
                outpuMessages++
            }
            if (mess.userFromId == partnerUserId && mess.userToId == myUserId) {
                inputMessages++
            }
        }
        return "${inputMessages + outpuMessages}:${inputMessages}:${outpuMessages}"
    }

    fun countOfReadMessagesByUsersId(
        myUserId: Int,
        partnerUserId: Int
    ): String {         // посчитаем, сколько НЕпрочитанных сообщений в чате
        var inputMessages = 0
        var outpuMessages = 0
        messages.forEach { mess ->
            if (mess.userFromId == myUserId && mess.userToId == partnerUserId && !mess.readIt) {
                outpuMessages++
            }
            if (mess.userFromId == partnerUserId && mess.userToId == myUserId && !mess.readIt) {
                inputMessages++
            }
        }
        return "${inputMessages + outpuMessages}:${inputMessages}:${outpuMessages}"
    }

    fun killMessagesByUsersId(
        myUserId: Int,
        partnerUserId: Int
    ) {   // Удаляем сообщения одного чата (для 2-х пользователей)
        messages.removeIf { it.userToId == partnerUserId && it.userFromId == myUserId }
        messages.removeIf { it.userToId == myUserId && it.userFromId == partnerUserId }
    }

    fun killMessageByMessageId(Id: Int) {   // Удаляем сообщение по id
        messages.removeIf { it.id == Id }
    }
}

data class Users(
    val id: Int = 0,            // Идентификатор записи.
    val name: String            // Имя пользователя
)

data class Message(
    val id: Int = 0,            // Идентификатор записи.
    val userFromId: Int,        // От кого
    val userToId: Int,          // Кому
    val text: String,           // Текст сообщения.
    val readIt: Boolean,        // Прочитано получателем или нет
)

fun MutableList<Message>.setMessageReadByMessageId_ext(id: Int) {      // устанавливаем сообщения "прочитанными" через Extension Functions
    for ((index, mess) in this.withIndex()) {
        if (mess.id == id) {
            this[index] = mess.copy(readIt = true)
        }
    }
}

fun MutableList<Message>.editMessageByMessageId_ext(
    id: Int,
    text: String
) {          // редактируем сообщение через Extension Functions
    for ((index, mess) in this.withIndex()) {
        if (mess.id == id) {
            this[index] = mess.copy(text = text)
        }
    }
}

