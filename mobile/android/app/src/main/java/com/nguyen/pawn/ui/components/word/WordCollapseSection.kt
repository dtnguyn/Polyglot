package com.nguyen.pawn.ui.components.word

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.nguyen.pawn.ui.theme.ReallyRed
import com.nguyen.pawn.ui.theme.Typography

@Composable
fun WordCollapseSection(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        backgroundColor = ReallyRed,
        elevation = 0.dp,
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Pepper", style = Typography.h1, color = Color.White)
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = "UK /ˈpepə(r)/",
                style = Typography.body2,
                color = Color.White
            )
            Text(
                text = "UK /ˈpepə(r)/",
                style = Typography.body2,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
}