package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.Project_Spark.ui.theme.ProjectSparkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                ImageViewExample()
                MainScreen()
            }
        }
    }
}
@Composable
fun ImageViewExample() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.sparklogo),
            contentDescription = "logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(top = 180.dp, start = 26.dp, end = 26.dp)
                .width(380.dp)
                .height(111.dp)
        )
    }
}

@Composable
fun MainScreen() {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            },
          modifier = Modifier
       .width(500.dp) // 버튼 너비 설정
       .height(60.dp) // 버튼 높이 설정{

        ) {
            Text(
                text = "이메일로 로그인하기" ,
                style = androidx.compose.ui.text.TextStyle(
                fontSize =24.sp ),
                fontFamily=fontFamily)// 텍스트 크기를 24sp로 설정)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("회원가입",fontFamily=fontFamily,style = androidx.compose.ui.text.TextStyle(
                fontSize =24.sp ),)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectSparkTheme {
        ImageViewExample()
        MainScreen()
    }
}
