package com.moderndev.polyglot.ui.components.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moderndev.polyglot.model.Feed
import com.moderndev.polyglot.ui.theme.Typography
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import com.moderndev.polyglot.R

@Composable
fun FeedItem(feed: Feed, onClick: (feed: Feed) -> Unit, onShare: (feedUrl: String) -> Unit) {

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(15.dp))

            .clickable {
                onClick(feed)
            }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            if (feed.type == "news") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.news_icon),
                        contentDescription = "news_icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 5.dp)
                    )
                    Text(text = stringResource(id = R.string.news), style = Typography.subtitle1)
                }
                Text(text = feed.title, style = Typography.h5)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = feed.publishedDate ?: "",
                    style = Typography.subtitle2
                )

                Spacer(modifier = Modifier.padding(5.dp))

                GlideImage(
                    imageModel = feed.thumbnail ?: "",
                    alignment = Alignment.Center,
                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                    contentScale = ContentScale.FillWidth,
                    // shows an image with a circular revealed animation.
                    circularReveal = CircularReveal(duration = 250),
                    // shows a placeholder ImageBitmap when loading.
//                    placeHolder = ImageBitmap.imageResource(id = R.drawable.),
                    // shows an error ImageBitmap when the request failed.
                    error = ImageBitmap.imageResource(id = R.drawable.image_loading_error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                )


                Spacer(modifier = Modifier.padding(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    onShare(feed.url)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.share),
                        contentDescription = "share_icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 5.dp)
                    )
                    Text(text = stringResource(id = R.string.share), style = Typography.subtitle1)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.video),
                        contentDescription = "news_icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 5.dp)
                    )
                    Text(text = stringResource(id = R.string.video), style = Typography.subtitle1)
                }
                Spacer(modifier = Modifier.padding(5.dp))

                Box(contentAlignment = Alignment.Center) {
                    GlideImage(
                        imageModel = feed.thumbnail ?: "",
                        alignment = Alignment.Center,
                        // Crop, Fit, Inside, FillHeight, FillWidth, None
                        contentScale = ContentScale.FillWidth,
                        // shows an image with a circular revealed animation.
                        circularReveal = CircularReveal(duration = 250),
                        // shows a placeholder ImageBitmap when loading.
//                        placeHolder = ImageBitmap.imageResource(id = R.drawable.cat_loading_icon),
                        // shows an error ImageBitmap when the request failed.
                        error = ImageBitmap.imageResource(id = R.drawable.image_loading_error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                    )
                    Image(
                        painter = painterResource(id = R.drawable.play_icon),
                        contentDescription = "news_icon",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 5.dp)
                    )
                }


                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = feed.title, style = Typography.h5)
                Spacer(modifier = Modifier.padding(2.dp))

                Text(
                    text = feed.publishedDate ?: "",
                    style = Typography.subtitle2
                )

                Spacer(modifier = Modifier.padding(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.share),
                        contentDescription = "share_icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 5.dp)
                    )
                    Text(text = stringResource(id = R.string.share), style = Typography.subtitle1)
                }

            }

        }
    }
}