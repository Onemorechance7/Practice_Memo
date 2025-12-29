package com.example.practice_memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.practice_memo.ui.theme.PracticeMemoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticeMemoTheme {
                MemoApp()
            }
        }
    }
}

private data class Memo(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: Long
)

private sealed class Screen {
    data object List : Screen()
    data class Edit(val memoId: Int) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoApp() {
    var memos by rememberSaveable { mutableStateOf(listOf<Memo>()) }
    var screen by rememberSaveable { mutableStateOf<Screen>(Screen.List) }
    var nextId by rememberSaveable { mutableIntStateOf(1) }

    when (val currentScreen = screen) {
        Screen.List -> {
            val appBarState = rememberTopAppBarState()
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "메모",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        scrollBehavior = null
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            val newMemo = Memo(
                                id = nextId,
                                title = "새 메모",
                                content = "",
                                createdAt = System.currentTimeMillis()
                            )
                            nextId += 1
                            memos = listOf(newMemo) + memos
                            screen = Screen.Edit(newMemo.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = "새 메모"
                        )
                    }
                }
            ) { paddingValues ->
                MemoList(
                    memos = memos,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onMemoClick = { memoId -> screen = Screen.Edit(memoId) }
                )
            }
        }

        is Screen.Edit -> {
            val memo = memos.firstOrNull { it.id == currentScreen.memoId }
            if (memo == null) {
                screen = Screen.List
                return
            }
            MemoEditor(
                memo = memo,
                onSave = { updatedMemo ->
                    memos = memos.map { existing ->
                        if (existing.id == updatedMemo.id) updatedMemo else existing
                    }
                    screen = Screen.List
                },
                onBack = { screen = Screen.List }
            )
        }
    }
}

@Composable
private fun MemoList(
    memos: List<Memo>,
    modifier: Modifier = Modifier,
    onMemoClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "당신의 오늘을 기록하세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (memos.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(memos, key = { it.id }) { memo ->
                    MemoCard(
                        memo = memo,
                        onClick = { onMemoClick(memo.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "아직 작성된 메모가 없어요.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "오른쪽 아래 버튼으로 첫 메모를 시작해보세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun MemoCard(memo: Memo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = memo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = memo.content.ifBlank { "내용을 입력해보세요." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatDate(memo.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoEditor(
    memo: Memo,
    onSave: (Memo) -> Unit,
    onBack: () -> Unit
) {
    var title by remember(memo.id) { mutableStateOf(memo.title) }
    var content by remember(memo.id) { mutableStateOf(memo.content) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "메모 편집") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    onSave(
                        memo.copy(
                            title = title.ifBlank { "제목 없음" },
                            content = content
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "저장")
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    return formatter.format(Date(timestamp))
}
