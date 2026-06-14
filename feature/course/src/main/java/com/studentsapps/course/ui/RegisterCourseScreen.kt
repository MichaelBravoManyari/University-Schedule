package com.studentsapps.course.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studentsapps.course.R
import com.studentsapps.course.viewmodels.RegisterCourseViewModel
import theme.UniversityScheduleTheme

// ================================
// STATEFUL SCREEN
// ================================

/**
 * Adaptive "Register Course" screen connected to [RegisterCourseViewModel].
 *
 * Handles all side effects (one-shot navigation, Toast errors) and delegates
 * purely visual decisions to the stateless overload below.
 *
 * @param viewModel           The course registration ViewModel.
 * @param onCourseSaved       Invoked after a successful save/update.
 * @param onCourseDeleted     Invoked after a successful deletion.
 * @param onColorPickerClick  Invoked when the user taps the color row;
 *                            receives the current ARGB color so the bottom-sheet
 *                            can pre-select the right swatch.
 * @param onCancelClick       Invoked when the user taps "Cancelar".
 * @param modifier            Optional modifier for the screen root.
 */
@Composable
fun RegisterCourseScreen(
    viewModel: RegisterCourseViewModel,
    onCourseSaved: () -> Unit,
    onCourseDeleted: () -> Unit,
    onColorPickerClick: (colorArgb: Int) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // One-shot: course saved / updated
    LaunchedEffect(uiState.isCourseRecorded) {
        if (uiState.isCourseRecorded) {
            onCourseSaved()
            //viewModel.onNavigationComplete()
        }
    }

    // One-shot: course deleted
    LaunchedEffect(uiState.isCourseDeleted) {
        if (uiState.isCourseDeleted) {
            onCourseDeleted()
            //viewModel.onNavigationComplete()
        }
    }

    // One-shot: error Toast
//    LaunchedEffect(uiState.error) {
//        uiState.error?.let { error ->
//            val message = when (error) {
//                is RegisterCourseUiError.Message -> context.getString(error.resId)
//                is RegisterCourseUiError.Text    -> error.message
//            }
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//            viewModel.dismissError()
//        }
//    }

    RegisterCourseScreen(
        courseName = uiState.name,
        teacherName = uiState.nameProfessor.orEmpty(),
        selectedColor = Color(uiState.color),
        courseNameError = uiState.courseNameError,
        isEditMode = uiState.courseId.isNotEmpty(),
        isLoading = false,
        onCourseNameChange = viewModel::setCourseName,
        onTeacherNameChange = viewModel::setNameProfessor,
        onColorPickerClick = { onColorPickerClick(uiState.color) },
        onSaveClick = {
            if (uiState.courseId.isNotEmpty()) viewModel.updateCourse()
            else viewModel.registerCourse()
        },
        onDeleteClick = viewModel::deleteCourse,
        onCancelClick = onCancelClick,
        modifier = modifier,
    )
}

// ================================
// STATELESS SCREEN  (previews / testing)
// ================================

/**
 * Stateless version of the Register Course screen.
 *
 * Receives all state as plain parameters and fires callbacks for every user
 * interaction.  This version has no knowledge of ViewModels or navigation.
 *
 * @param courseName          Current course name text.
 * @param teacherName         Current teacher name text.
 * @param selectedColor       Currently selected course color.
 * @param courseNameError     True when the course-name field has a validation error.
 * @param isEditMode          True when editing an existing course (shows "Eliminar" button
 *                            and changes save label to "Actualizar").
 * @param isLoading           True while an async operation is in progress.
 * @param onCourseNameChange  Callback for course-name field changes.
 * @param onTeacherNameChange Callback for teacher-name field changes.
 * @param onColorPickerClick  Callback when the user taps the color row.
 * @param onSaveClick         Callback for the primary save/update action.
 * @param onDeleteClick       Callback for the delete action (edit mode only).
 * @param onCancelClick       Callback for the cancel/back action.
 * @param modifier            Optional modifier.
 */
@Composable
fun RegisterCourseScreen(
    courseName: String,
    teacherName: String,
    selectedColor: Color,
    courseNameError: Boolean,
    isEditMode: Boolean,
    isLoading: Boolean,
    onCourseNameChange: (String) -> Unit,
    onTeacherNameChange: (String) -> Unit,
    onColorPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        when {
            // Phone in landscape (compact height < 480 dp) → two-column layout
            !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_COMPACT_THRESHOLD) -> {
                CompactHeightLayout(
                    courseName = courseName,
                    teacherName = teacherName,
                    selectedColor = selectedColor,
                    courseNameError = courseNameError,
                    isEditMode = isEditMode,
                    onCourseNameChange = onCourseNameChange,
                    onTeacherNameChange = onTeacherNameChange,
                    onColorPickerClick = onColorPickerClick,
                    onSaveClick = onSaveClick,
                    onDeleteClick = onDeleteClick,
                    onCancelClick = onCancelClick,
                )
            }
            // Default: phone in portrait → single-column layout
            else -> {
                CompactLayout(
                    courseName = courseName,
                    teacherName = teacherName,
                    selectedColor = selectedColor,
                    courseNameError = courseNameError,
                    isEditMode = isEditMode,
                    onCourseNameChange = onCourseNameChange,
                    onTeacherNameChange = onTeacherNameChange,
                    onColorPickerClick = onColorPickerClick,
                    onSaveClick = onSaveClick,
                    onDeleteClick = onDeleteClick,
                    onCancelClick = onCancelClick,
                )
            }
        }
    }
}

// ================================
// LAYOUTS
// ================================

/**
 * Single-column layout for phones in portrait orientation.
 *
 * Stack order (top → bottom):
 * 1. App icon + screen title + subtitle  (branding column)
 * 2. [CourseFormContent]                 (fields + color row + actions)
 */
@Composable
private fun CompactLayout(
    courseName: String,
    teacherName: String,
    selectedColor: Color,
    courseNameError: Boolean,
    isEditMode: Boolean,
    onCourseNameChange: (String) -> Unit,
    onTeacherNameChange: (String) -> Unit,
    onColorPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SECTION_SPACING, Alignment.CenterVertically),
    ) {
        CourseHeaderSection()

        CourseFormContent(
            courseName = courseName,
            teacherName = teacherName,
            selectedColor = selectedColor,
            courseNameError = courseNameError,
            isEditMode = isEditMode,
            onCourseNameChange = onCourseNameChange,
            onTeacherNameChange = onTeacherNameChange,
            onColorPickerClick = onColorPickerClick,
            onSaveClick = onSaveClick,
            onDeleteClick = onDeleteClick,
            onCancelClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Two-column layout for phones in landscape mode (compact height < 480 dp).
 *
 * Left  → [CourseHeaderSection] (icon + title + subtitle), vertically centred.
 * Right → [CourseFormContent] (fields + color row + actions), scrollable.
 *
 * Both columns share equal weight so they divide the width evenly.
 */
@Composable
private fun CompactHeightLayout(
    courseName: String,
    teacherName: String,
    selectedColor: Color,
    courseNameError: Boolean,
    isEditMode: Boolean,
    onCourseNameChange: (String) -> Unit,
    onTeacherNameChange: (String) -> Unit,
    onColorPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(SCREEN_PADDING),
        horizontalArrangement = Arrangement.spacedBy(SECTION_SPACING, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: branding
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CourseHeaderSection()
        }

        // Right: scrollable form
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FIELD_SPACING, Alignment.CenterVertically),
        ) {
            CourseFormContent(
                courseName = courseName,
                teacherName = teacherName,
                selectedColor = selectedColor,
                courseNameError = courseNameError,
                isEditMode = isEditMode,
                onCourseNameChange = onCourseNameChange,
                onTeacherNameChange = onTeacherNameChange,
                onColorPickerClick = onColorPickerClick,
                onSaveClick = onSaveClick,
                onDeleteClick = onDeleteClick,
                onCancelClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ================================
// SHARED FORM SECTION
// ================================

/**
 * All form widgets grouped together so both layouts share the exact same
 * composable tree — only the surrounding scaffold differs.
 *
 * Contents:
 * 1. [CourseNameField]
 * 2. [TeacherNameField]
 * 3. [CourseColorRow]
 * 4. (edit-mode) [DeleteCourseButton]
 * 5. [SaveButton]
 * 6. [CancelButton]
 */
@Composable
private fun CourseFormContent(
    courseName: String,
    teacherName: String,
    selectedColor: Color,
    courseNameError: Boolean,
    isEditMode: Boolean,
    onCourseNameChange: (String) -> Unit,
    onTeacherNameChange: (String) -> Unit,
    onColorPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FIELD_SPACING),
    ) {
        CourseNameField(
            value = courseName,
            onValueChange = onCourseNameChange,
            isError = courseNameError,
            onNext = {},   // focus moves automatically via ImeAction.Next
        )

        TeacherNameField(
            value = teacherName,
            onValueChange = onTeacherNameChange,
            onDone = onSaveClick,
        )

        CourseColorRow(
            selectedColor = selectedColor,
            onClick = onColorPickerClick,
        )

        Spacer(modifier = Modifier.height(SECTION_SPACING))

        if (isEditMode) {
            DeleteCourseButton(onClick = onDeleteClick)
        }

        SaveButton(
            isEditMode = isEditMode,
            onClick = onSaveClick,
        )

        CancelButton(onClick = onCancelClick)
    }
}

// ================================
// BRANDING SECTION
// ================================

/**
 * App icon, screen title ("Añadir Curso"), and subtitle.
 */
@Composable
private fun CourseHeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        // App / module icon
        Box(
            modifier = Modifier
                .size(ICON_SIZE)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.onSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                // Replace with your actual course drawable, e.g.:
                // painter = painterResource(R.drawable.ic_course),
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(modifier = Modifier.height(SMALL_SPACING))

        Text(
            text = stringResource(R.string.add_course),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.add_course_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ================================
// FIELD COMPOSABLES
// ================================

/**
 * "Nombre del Curso" text field.
 *
 * Shows a trailing error icon when [isError] is true, mirroring
 * the original XML `endIconDrawable` behaviour.
 *
 * @param value       Current field value.
 * @param onValueChange Callback fired on every keystroke.
 * @param isError     True when the field has a validation error.
 * @param onNext      Callback when the user presses the "Next" IME key.
 *                    Typically unused here because [ImeAction.Next] moves focus
 *                    automatically via [KeyboardActions], but exposed for testing.
 * @param modifier    Optional modifier.
 */
@Composable
private fun CourseNameField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.course_name)) },
        placeholder = { Text(stringResource(R.string.course_name_placeholder)) },
        isError = isError,
        supportingText = if (isError) {
            { Text(stringResource(R.string.course_name_error)) }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
        ),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

/**
 * "Nombre del Profesor" text field.
 *
 * [ImeAction.Done] triggers [onDone] (typically the save action),
 * allowing keyboard-only submission.
 *
 * @param value         Current field value.
 * @param onValueChange Callback fired on every keystroke.
 * @param onDone        Callback when the user presses the "Done" IME key.
 * @param modifier      Optional modifier.
 */
@Composable
private fun TeacherNameField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.teacher_name)) },
        placeholder = { Text(stringResource(R.string.teacher_name_placeholder)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

// ================================
// COLOR ROW
// ================================

/**
 * Tappable row that shows the current course colour and a chevron.
 *
 * Tapping the row opens the colour bottom-sheet via [onClick].
 * The colour is represented as a filled circle, matching the original XML design.
 *
 * @param selectedColor Current course colour.
 * @param onClick       Callback invoked when the row is tapped.
 * @param modifier      Optional modifier.
 */
@Composable
private fun CourseColorRow(
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.course_color),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Colour circle
            Box(
                modifier = Modifier
                    .size(COLOR_CIRCLE_SIZE)
                    .clip(MaterialTheme.shapes.extraLarge)   // circle
                    .background(selectedColor),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = stringResource(R.string.select_course_color),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ================================
// ACTION BUTTONS
// ================================

/**
 * Primary action button.
 * - Add mode:  label "Guardar"
 * - Edit mode: label "Actualizar"
 */
@Composable
private fun SaveButton(
    isEditMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = stringResource(
                if (isEditMode) R.string.update else R.string.save,
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * "Cancelar" secondary text button.
 */
@Composable
private fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT),
    ) {
        Text(
            text = stringResource(R.string.cancel),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * "Eliminar Curso" destructive button (edit mode only).
 * Uses [MaterialTheme.colorScheme.error] as container colour to signal danger.
 */
@Composable
private fun DeleteCourseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = stringResource(R.string.delete_course),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ================================
// SCREEN-SPECIFIC CONSTANTS
// ================================

private val SCREEN_PADDING  = 24.dp
private val SECTION_SPACING = 24.dp
private val SMALL_SPACING   = 8.dp
private val FIELD_SPACING   = 8.dp
private val BUTTON_HEIGHT   = 56.dp
private val ICON_SIZE       = 72.dp
private val COLOR_CIRCLE_SIZE = 24.dp
private const val HEIGHT_COMPACT_THRESHOLD = 480

// ================================
// PREVIEWS
// ================================

@PreviewScreenSizes
@Composable
private fun RegisterCourseScreenAddModePreview() {
    UniversityScheduleTheme {
        RegisterCourseScreen(
            courseName = "",
            teacherName = "",
            selectedColor = Color(0xFF26A69A),
            courseNameError = false,
            isEditMode = false,
            isLoading = false,
            onCourseNameChange = {},
            onTeacherNameChange = {},
            onColorPickerClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onCancelClick = {},
        )
    }
}

@PreviewScreenSizes
@Composable
private fun RegisterCourseScreenEditModePreview() {
    UniversityScheduleTheme {
        RegisterCourseScreen(
            courseName = "Cálculo Integral",
            teacherName = "Dr. Roberto Silva",
            selectedColor = Color(0xFF26A69A),
            courseNameError = false,
            isEditMode = true,
            isLoading = false,
            onCourseNameChange = {},
            onTeacherNameChange = {},
            onColorPickerClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onCancelClick = {},
        )
    }
}

@PreviewScreenSizes
@Composable
private fun RegisterCourseScreenErrorPreview() {
    UniversityScheduleTheme {
        RegisterCourseScreen(
            courseName = "",
            teacherName = "",
            selectedColor = Color(0xFF26A69A),
            courseNameError = true,
            isEditMode = false,
            isLoading = false,
            onCourseNameChange = {},
            onTeacherNameChange = {},
            onColorPickerClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onCancelClick = {},
        )
    }
}