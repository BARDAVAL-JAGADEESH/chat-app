

package com.bardaval.chattingapplication

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bardaval.chattingapplication.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LivechatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgessChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null

    val status= mutableStateOf<List<Status>>(listOf())
    val inProgressStatus= mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null

        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun populateMessages(chatId: String) {
        inProgessChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    handleException(error)
                    inProgessChatMessage.value = false
                    return@addSnapshotListener
                }

                value?.let {
                    chatMessages.value = it.documents.mapNotNull { doc ->
                        doc.toObject<Message>()
                    }.sortedBy { message ->
                        message.timestamp
                    }
                }

                inProgessChatMessage.value = false
            }
    }

    fun depopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListener?.remove() // Ensure to remove the listener
        currentChatMessageListener = null
    }

    fun populateChat() {
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
                inProcessChats.value = false
                return@addSnapshotListener
            }

            value?.let {
                chats.value = it.documents.mapNotNull { doc ->
                    doc.toObject<ChatData>()
                }
            }

            inProcessChats.value = false
        }
    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(sendby = userData.value?.userId, message = message, timestamp = time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
            .addOnFailureListener { handleException(it, "Failed to send message") }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        }

        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signIn.value = true
                                createOrUpdateProfile(name, number)
                            } else {
                                handleException(task.exception, customMessage = "Sign Up failed")
                            }
                            inProgress.value = false
                        }
                } else {
                    handleException(customMessage = "Number already exists")
                    inProgress.value = false
                }
            }
            .addOnFailureListener {
                handleException(it)
                inProgress.value = false
            }
    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        }

        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signIn.value = true
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(task.exception, customMessage = "Login Failed")
                }
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) { downloadUrl ->
            createOrUpdateProfile(imageUrl = downloadUrl.toString())
        }
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener(onSuccess)
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
                inProgress.value = false
            }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val updatedUserData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        inProgress.value = true
        db.collection(USER_NODE).document(uid).set(updatedUserData)
            .addOnSuccessListener {
                getUserData(uid)
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it, "Can't retrieve user")
                inProgress.value = false
            }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).get()
            .addOnSuccessListener { document ->
                userData.value = document.toObject<UserData>()
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it, "Can't retrieve user data")
                inProgress.value = false
                populateChat()
                populateStatuses()

            }
    }

    private fun handleException(exception: Exception? = null, customMessage: String? = null) {
        exception?.let {
            Log.e("LivechatViewModel", it.message ?: "Unknown error")
        }
        customMessage?.let {
            Log.e("LivechatViewModel", it)
            eventMutableState.value = Events(it)
        }
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener=null
        eventMutableState.value = Events("Logged Out")
    }
    fun onAddChat(number: String) {
        if (number.isEmpty() || !number.matches(Regex("\\d+"))) {
            handleException(customMessage = "Number must contain digits only")
            return
        }

        val currentUserNumber = userData.value?.number ?: return
        if (number == currentUserNumber) {
            handleException(customMessage = "You cannot chat with yourself")
            return
        }

        db.collection(CHATS).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.number", number),
                    Filter.equalTo("user2.number", currentUserNumber)
                ),
                Filter.and(
                    Filter.equalTo("user2.number", number),
                    Filter.equalTo("user1.number", currentUserNumber)
                )
            )
        ).get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener { userSnapshot ->
                    if (userSnapshot.isEmpty) {
                        handleException(customMessage = "Number not found")
                    } else {
                        val chatPartner = userSnapshot.toObjects<UserData>()[0]
                        val chatId = db.collection(CHATS).document().id
                        val chat = ChatData(
                            chatId = chatId,
                            user1 = ChatUser(
                                userData.value?.userId,
                                userData.value?.name,
                                userData.value?.imageUrl,
                                userData.value?.number
                            ),
                            user2 = ChatUser(
                                chatPartner.userId,
                                chatPartner.name,
                                chatPartner.imageUrl,
                                chatPartner.number
                            )
                        )
                        db.collection(CHATS).document(chatId).set(chat)
                            .addOnSuccessListener {
                                Log.d("LiveChatApp", "Chat created successfully")
                            }
                            .addOnFailureListener {
                                handleException(it, "Failed to create chat")
                            }
                    }
                }.addOnFailureListener {
                    handleException(it)
                }
            } else {
                handleException(customMessage = "Chat already exists")
            }
        }.addOnFailureListener {
            handleException(it)
        }
    }

    fun uploadStatus(uri: Uri) {

        uploadImage(uri){
            createStatus(it.toString())

        }

    }

    fun createStatus(imageUrl: String){
        val nextStatus=Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number,
            ),
            imageUrl,
            System.currentTimeMillis().toString()
        )
        db.collection(STATUS).document().set(nextStatus)
    }

    fun populateStatuses()
    {
        val timeDlta=24L*60*60*1000
        val cutoff=System.currentTimeMillis()-timeDlta
        inProgressStatus.value=true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",userData.value?.userId),
                Filter.equalTo("user2.userId",userData.value?.userId)
            )
        ).addSnapshotListener{
                value,error->
            if (error!=null)
                handleException(error)
            if (value!=null){
                val currentConnetion= arrayListOf(userData.value?.userId)
                val chats=value.toObjects<ChatData>()
                chats.forEach{
                        chat->
                    if (chat.user1.userId==userData.value?.userId)
                    {
                        currentConnetion.add(chat.user2.userId)
                    }
                    else
                        currentConnetion.add(chat.user1.userId)
                }
                db.collection(STATUS).whereGreaterThan("timestamp",cutoff).whereIn("user.userId",currentConnetion)
                    .addSnapshotListener{
                            value,error ->
                        if (error!=null){
                            handleException(error)
                        }
                        if (value!=null)

                        {
                            status.value=value.toObjects()
                            inProgressStatus.value=false
                        }
                    }
            }
        }
    }
}

