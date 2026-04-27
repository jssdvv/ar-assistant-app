package com.jssdvv.ara.core.presentation.common.component

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R

@Composable
fun ArrowDownIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_arrow_down),
    contentDescription: String = stringResource(R.string.icon_arrow_down_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun ArrowDropDownIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_arrow_drop_down),
    contentDescription: String = stringResource(R.string.icon_arrow_drop_down_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun ArrowDownwardsIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_arrow_downward),
    contentDescription: String = stringResource(R.string.icon_arrow_downward_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_arrow_back),
    contentDescription: String = stringResource(R.string.icon_arrow_back_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun ArrowPreviousItemIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_arrow_previous_item),
    contentDescription: String = stringResource(R.string.icon_arrow_previous_item_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_check),
    contentDescription: String = stringResource(R.string.icon_check_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun CheckListIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_checklist),
    contentDescription: String = stringResource(R.string.icon_check_list_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_close),
    contentDescription: String = stringResource(R.string.icon_close_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun EditIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_edit),
    contentDescription: String = stringResource(R.string.icon_edit_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_add),
    contentDescription: String = stringResource(R.string.icon_add_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun RemoveIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_remove),
    contentDescription: String = stringResource(R.string.icon_remove_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_search),
    contentDescription: String = stringResource(R.string.icon_search_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_share),
    contentDescription: String = stringResource(R.string.icon_share_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun TextIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_text),
    contentDescription: String = stringResource(R.string.icon_text_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun MenuCloseIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_menu_close),
    contentDescription: String = stringResource(R.string.icon_menu_close_content_desc)
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription
)

@Composable
fun MenuIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_menu_open),
    contentDescription: String = stringResource(R.string.icon_menu_open_content_desc)
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription
)

@Composable
fun DeleteIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_delete),
    contentDescription: String = stringResource(R.string.icon_delete_content_desc),
    tint: Color = LocalContentColor.current
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
    tint = tint
)

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_warning),
    contentDescription: String = stringResource(R.string.icon_warning_content_desc)
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription
)

@Composable
fun VisibleOnIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_visibility_on),
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
    tint = tint
)

@Composable
fun VisibleOffIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_visibility_off),
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
    tint = tint
)