package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.User
import com.example.ui.theme.*

@Composable
fun ChatScreen(
    conversations: List<Conversation>,
    activeConversationId: String?,
    messages: Map<String, List<ChatMessage>>,
    currentUser: User,
    language: String,
    onSelectConversation: (String) -> Unit,
    onCloseActiveChat: () -> Unit,
    onSendMessage: (String, String) -> Unit,
    onReportUser: (String, String) -> Unit = { _, _ -> },
    onBlockUser: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"

    if (activeConversationId != null) {
        val conv = conversations.find { it.id == activeConversationId } ?: Conversation(
            id = activeConversationId,
            otherUserId = "other",
            otherUserName = "Seller / User",
            lastMessage = ""
        )
        val chatMessages = messages[activeConversationId] ?: emptyList()

        ActiveChatRoom(
            conversation = conv,
            messages = chatMessages,
            currentUserId = currentUser.id,
            isHindi = isHindi,
            onBack = onCloseActiveChat,
            onSendMessage = { text -> onSendMessage(activeConversationId, text) },
            onReportUser = onReportUser,
            onBlockUser = { userId, name ->
                onBlockUser(userId, name)
                onCloseActiveChat()
            }
        )
    } else {
        // Conversation List
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("conversations_list_screen")
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "संदेश और बातचीत (Messages & Inquiries)" else "Messages & Inquiries",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (conversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "कोई सक्रिय बातचीत नहीं है" else "No active conversations",
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(conversations) { conv ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("conversation_item_${conv.id}")
                                .clickable { onSelectConversation(conv.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar with online dot
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(SaffronContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = conv.otherUserName.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = OnSaffronContainer,
                                            fontSize = 18.sp
                                        )
                                    }
                                    if (conv.isOnline) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(IndiaGreen)
                                                .align(Alignment.BottomEnd)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = conv.otherUserName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (conv.unreadCount > 0) {
                                            Surface(
                                                shape = CircleShape,
                                                color = SaffronPrimary
                                            ) {
                                                Text(
                                                    text = "${conv.unreadCount}",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (conv.relatedListingTitle != null) {
                                        Text(
                                            text = "📦 ${conv.relatedListingTitle}",
                                            fontSize = 11.sp,
                                            color = BharatBlue,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                    }

                                    Text(
                                        text = conv.lastMessage,
                                        fontSize = 12.sp,
                                        color = SlateTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveChatRoom(
    conversation: Conversation,
    messages: List<ChatMessage>,
    currentUserId: String,
    isHindi: Boolean,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onReportUser: (String, String) -> Unit = { _, _ -> },
    onBlockUser: (String, String) -> Unit = { _, _ -> }
) {
    var textInput by remember { mutableStateOf("") }
    var showChatMenu by remember { mutableStateOf(false) }
    val quickReplies = listOf(
        if (isHindi) "क्या कीमत में कुछ छूट संभव है?" else "Is the price negotiable?",
        if (isHindi) "मैं कमरा/सामान कब देख सकता हूँ?" else "When can I visit?",
        if (isHindi) "कृपया अपना स्थान/लैंडमार्क शेयर करें" else "Please share exact location",
        if (isHindi) "क्या यह अभी उपलब्ध है?" else "Is this available?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_chat_room")
    ) {
        // Chat Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SaffronPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.otherUserName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = conversation.otherUserName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (conversation.isOnline) "🟢 ऑनलाइन (Online)" else "ऑफलाइन",
                        fontSize = 10.sp,
                        color = if (conversation.isOnline) IndiaGreenDark else SlateTextMuted
                    )
                }

                // 3-dots Menu for Report / Block User
                Box {
                    IconButton(onClick = { showChatMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showChatMenu,
                        onDismissRequest = { showChatMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "🚩 यूज़र रिपोर्ट करें" else "🚩 Report user") },
                            onClick = {
                                showChatMenu = false
                                onReportUser(conversation.otherUserId, conversation.otherUserName)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "🚫 यूज़र ब्लॉक करें" else "🚫 Block user", color = BreakingNewsRed) },
                            onClick = {
                                showChatMenu = false
                                onBlockUser(conversation.otherUserId, conversation.otherUserName)
                            }
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        color = if (isMe) SaffronPrimary else MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (msg.listingTitle != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isMe) SaffronDark else SaffronContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = "📦 ${msg.listingTitle}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMe) Color.White else OnSaffronContainer
                                        )
                                        if (msg.listingPrice != null) {
                                            Text(
                                                text = msg.listingPrice,
                                                fontSize = 10.sp,
                                                color = if (isMe) SaffronLight else SaffronDark
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = msg.text,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Inquiries Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickReplies.take(2).forEach { reply ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateSurfaceVariant,
                    modifier = Modifier.clickable {
                        onSendMessage(reply)
                    }
                ) {
                    Text(
                        text = reply,
                        fontSize = 10.sp,
                        color = SlateTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }

        // Chat Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text(if (isHindi) "संदेश लिखें..." else "Type message...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .testTag("send_chat_msg_btn")
                        .size(44.dp)
                        .background(SaffronPrimary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}
