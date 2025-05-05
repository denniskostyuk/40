import org.junit.Test
import org.junit.Assert.*

class ChatServiceTest {

@org.junit.Test
 fun tryAddUser() {
 var chatService = ChatService(mutableListOf(), mutableListOf())
  val result = chatService.addUser("Петя")
 assertEquals(true, result)
 }

 @org.junit.Test
 fun tryAddDoubleUser() {
  var chatService = ChatService(mutableListOf(), mutableListOf())
  var result = chatService.addUser("Вася")
  result = chatService.addUser("Вася")
  assertEquals(false, result)
 }

 @org.junit.Test
 fun getUserName() {
  var chatService = ChatService(mutableListOf(), mutableListOf())
  chatService.addUser("Петя")
  chatService.addUser("Вася")
  chatService.addUser("Коля")

  var result = chatService.getUserNameById(2)

  assertEquals("Вася", result)
 }

}