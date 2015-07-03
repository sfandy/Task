package personal.andrewthompson.task.task;

import android.graphics.Color;

/**
 * Created by Andy on 6/9/15.
 *
 * These represent the constants for Task.
 */
public final class Constants {
    public static final String TASK_NAMES = "Task_Names_List";
    public static final String TASK_NOTES = "Task_Notes_List";
    public static final String TASK_NAME = "Task_Name";
    public static final String TASK_NOTE = "Task_Notes";
    public static final String ADD_TASK_FRAGMENT = "Add_Task_Fragment";
    public static final String EDIT_TASK_FRAGMENT = "Edit_Task_Fragment";
    public static final String TASK_LIST_POSITION = "Task_List_Position";
    public static final String LIST_COLOR = "List_Color";

    public static final Long TEST_SINGLE_HOUR = 1L;
    public static final Long DAY_IN_HOURS = 24L;
    public static final Long WEEK_IN_HOURS = 168L;
    public static final Long PAST_TASK_LIFESPAN = 504L;

    public static final int TASK_LIST_MONITOR_INTERVAL = 60000;
    public static final int ADD_TASK_BUTTON_OFFSET = 180;
    public static final int DIVIDER_HEIGHT = 2;

    /*
     * Color constants for divider colors.
     * Index 0 corresponds to the lowest level of task completion (red).
     * Index 20 corresponds to the highest level of task completion (green).
     */
    public static final int[] COLOR_LEVEL =
            {Color.parseColor("#FF0000"),
             Color.parseColor("#FF3700"),
             Color.parseColor("#FF5500"),
             Color.parseColor("#FF6600"),
             Color.parseColor("#FF7700"),
             Color.parseColor("#FF9100"),
             Color.parseColor("#FFA600"),
             Color.parseColor("#FFBB00"),
             Color.parseColor("#FFC400"),
             Color.parseColor("#FFD900"),
             Color.parseColor("#FFE100"),
             Color.parseColor("#FFEA00"),
             Color.parseColor("#FFF700"),
             Color.parseColor("#F2FF00"),
             Color.parseColor("#D4FF00"),
             Color.parseColor("#CCFF00"),
             Color.parseColor("#BBFF00"),
             Color.parseColor("#A2FF00"),
             Color.parseColor("#8CFF00"),
             Color.parseColor("#7BFF00"),
             Color.parseColor("#62FF00")};
}
