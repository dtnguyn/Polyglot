package com.moderndev.polyglot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moderndev.polyglot.R
import com.moderndev.polyglot.model.User
import com.moderndev.polyglot.ui.theme.*

@Composable
fun HomeAppBar(
    navController: NavController,
    user: User?,
    onAccountClick: () -> Unit,
    onSettingClick: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxWidth()
//            .height(120.dp)
            .padding(20.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user == null) {
            Column(
                modifier = Modifier
            ) {
                Text(text = stringResource(id = R.string.greeting), color = Color.White, style = Typography.h6)
                Spacer(modifier = Modifier.padding(5.dp))
                Button(
                    onClick = { navController.navigate("auth") },
                    colors = ButtonDefaults.buttonColors(ReallyRed),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Login", color = Color.White, style = Typography.h6)
                }
            }
        } else {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        context.resources.getIdentifier(
                            user.avatar ?: "human1", "drawable", "com.moderndev.polyglot"
                        )
                    ),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
                            onAccountClick()
                        })
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                ) {
                    Text(text = user.username, color = Color.White, style = Typography.h6)
                    Text(
                        text = stringResource(R.string.welcome_back),
                        color = Color.White,
                        style = Typography.body1
                    )
                }
            }

            RoundButton(
                backgroundColor = Color.White,
                size = 55.dp,
                icon = R.drawable.settings,
                padding = 12.dp,
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    onSettingClick()
                })

        }


    }

}