package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime

// ---------- Цвета (меняйте под себя) ----------
val Bg = Color(0xFF0F0F1A)
val Surface1 = Color(0xFF1A1A2E)
val Accent1 = Color(0xFF7C4DFF)
val Accent2 = Color(0xFF4FC3F7)
val Muted = Color(0xFF8E8EA9)

// ---------- Данные (пока заглушки, потом заменим на Firebase) ----------
data class Chat(val name: String, val last: String, val time: String, val unread: Int, val color: Color)
data class Msg(val text: String, val mine: Boolean, val time: String)

val demoChats = listOf(
    Chat("Алекс", "Скинь код, посмотрю", "12:41", 2, Color(0xFF7C4DFF)),
    Chat("Мария", "Договорились 👍", "11:20", 0, Color(0xFFFF6E91)),
    Chat("Команда", "Релиз завтра в 10:00", "Вчера", 5, Color(0xFF26C6A0)),
    Chat("Дима", "Голосовое сообщение", "Вчера", 0, Color(0xFFFFB74D)),
    Chat("Мама", "Не забудь поесть ❤️", "Пн", 1, Color(0xFF4FC3F7)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Accent1,
                    background = Bg,
                    surface = Surface1,
                    onBackground = Color.White,
                    onSurface = Color.White,
                )
            ) { App() }
        }
    }
}

@Composable
fun App() {
    var opened by remember { mutableStateOf<Chat?>(null) }
    BackHandler(enabled = opened != null) { opened = null }
    Crossfade(targetState = opened, label = "nav") { chat ->
        if (chat == null) ChatListScreen { opened = it }
        else ChatScreen(chat) { opened = null }
    }
}

// ---------- Список чатов ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onOpen: (Chat) -> Unit) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Чаты", fontWeight = FontWeight.Bold, fontSize = 26.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg, titleContentColor = Color.White),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}, containerColor = Accent1, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Edit, contentDescription = "Новый чат")
            }
        },
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            items(demoChats) { chat -> ChatRow(chat) { onOpen(chat) } }
        }
    }
}

@Composable
fun ChatRow(chat: Chat, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(chat, 52)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(chat.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color.White)
            Spacer(Modifier.height(2.dp))
            Text(chat.last, color = Muted, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(chat.time, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            if (chat.unread > 0) {
                Box(
                    Modifier.clip(CircleShape).background(Accent1).defaultMinSize(22.dp, 22.dp).padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("${chat.unread}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun Avatar(chat: Chat, size: Int) {
    Box(
        Modifier.size(size.dp).clip(CircleShape)
            .background(Brush.linearGradient(listOf(chat.color, chat.color.copy(alpha = 0.6f)))),
        contentAlignment = Alignment.Center,
    ) { Text(chat.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size / 2.4).sp) }
}

// ---------- Экран переписки ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chat: Chat, onBack: () -> Unit) {
    val messages = remember {
        mutableStateListOf(
            Msg("Привет! Как дела?", false, "12:01"),
            Msg("Привет! Делаю свой мессенджер 😎", true, "12:02"),
            Msg("Выглядит круто!", false, "12:03"),
        )
    }
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) { listState.animateScrollToItem(messages.lastIndex) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(chat, 38)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(chat.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                            Text("в сети", color = Accent2, fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface1, titleContentColor = Color.White),
            )
        },
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) { items(messages) { Bubble(it) } }

            Row(
                Modifier.fillMaxWidth().background(Bg).padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Сообщение", color = Muted) },
                    shape = RoundedCornerShape(26.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Surface1,
                        unfocusedContainerColor = Surface1,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Accent2,
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(50.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Accent1, Accent2)))
                        .clickable {
                            if (text.isNotBlank()) {
                                val t = LocalTime.now()
                                messages.add(Msg(text.trim(), true, "%02d:%02d".format(t.hour, t.minute)))
                                text = ""
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить", tint = Color.White) }
            }
        }
    }
}

@Composable
fun Bubble(msg: Msg) {
    val shape = RoundedCornerShape(
        topStart = 20.dp, topEnd = 20.dp,
        bottomStart = if (msg.mine) 20.dp else 4.dp,
        bottomEnd = if (msg.mine) 4.dp else 20.dp,
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.mine) Arrangement.End else Arrangement.Start) {
        Column(
            Modifier.widthIn(max = 290.dp).clip(shape)
                .background(
                    if (msg.mine) Brush.linearGradient(listOf(Accent1, Color(0xFF5B6CFF)))
                    else Brush.linearGradient(listOf(Surface1, Surface1))
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(msg.text, color = Color.White, fontSize = 16.sp)
            Text(
                msg.time, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
