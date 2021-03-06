package com.moderndev.polyglot.ui.components.feedDetail.news

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moderndev.polyglot.R
import com.moderndev.polyglot.ui.theme.LightGreen
import com.moderndev.polyglot.ui.theme.LightRed
import com.moderndev.polyglot.ui.theme.Typography

@Composable
fun WordActionMenu(
    word: String,
    onLookUpDefinition: (word: String) -> Unit,
    onLookUpImages: (word: String) -> Unit,
) {
    Column(Modifier.padding(20.dp)) {
        Text(text = "\"${word}\"", style = Typography.h2)
        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            onClick = {
                onLookUpDefinition(word)
            },
            content = {
                Text(text = stringResource(id = R.string.look_up_definitions), style = Typography.h6)
            },
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(LightRed),
        )

        Spacer(modifier = Modifier.padding(5.dp))
        Button(
            onClick = {
                onLookUpImages(word)
            },
            content = {
                Text(text = stringResource(id = R.string.find_images), style = Typography.h6)
            },
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(LightGreen),
        )
    }

}