package com.investwise_india.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.investwise_india.chatbot.MessageModel
import com.investwise_india.R
import com.investwise_india.chatbot.FundMeta
import com.investwise_india.ui.theme.ColorModelMessage
import com.investwise_india.ui.theme.ColorUserMessage
import com.investwise_india.ui.theme.Purple80
import com.investwise_india.ui.viewmodel.ChatViewModel
import kotlin.collections.reversed

//@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatPage(modifier: Modifier = Modifier,viewModel: ChatViewModel) {
    val fundMetaList = viewModel.fundMetaList.collectAsStateWithLifecycle().value

    Column(
        modifier = modifier
    ) {
        AppHeader()
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList,
            fundMetaList
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            }
        )
    }

}


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    fundMetaList: List<FundMeta>
) {
    if(messageList.isEmpty()){
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "Icon",
                tint = Purple80,
            )
            Text(text = "Ask me anything", fontSize = 22.sp)
        }
    }else{
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()){
                MessageRow(
                    messageModel = it,
                    fundMetaList = fundMetaList
                )
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel, fundMetaList: List<FundMeta>) {
    val isModel = messageModel.role=="model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {

                SelectionContainer {
//                    Text(
//                        text = messageModel.message,
//                        fontWeight = FontWeight.W500,
//                        color = Color.White
//                    )
                    val cleanText = messageModel.message
                        .replace("**", "")
                        .replace("* ", "\n• ")
                        .replace(Regex("\\b\\d{6}\\b")) { matchResult ->
                            val code = matchResult.value
                            val schemeName = fundMetaList.find { it.scheme_code.toString() == code }?.scheme_name
                            schemeName ?: code // fallback to the original code if no match found
                        }

                    Text(
                        text = cleanText,
                        fontWeight = FontWeight.W500,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        maxLines = Int.MAX_VALUE,
                        softWrap = true
                    )

                }


            }

        }


    }


}



@Composable
fun MessageInput(onMessageSend : (String)-> Unit) {

    var message by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            placeholder = {
                Text(text = "How may i assist you?")
            },
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if(message.isNotEmpty()){
                onMessageSend(message)
                message = ""
            }

        }) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send"
            )
        }
    }
}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Investment ChatBot",
            color = Color.White,
            fontSize = 22.sp
        )

    }
}

