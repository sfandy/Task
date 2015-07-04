package personal.andrewthompson.task.task;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import static personal.andrewthompson.task.task.Constants.COLOR_LEVEL;
import static personal.andrewthompson.task.task.Constants.DAY_IN_HOURS;
import static personal.andrewthompson.task.task.Constants.DIVIDER_HEIGHT;
import static personal.andrewthompson.task.task.Constants.EDIT_TASK_FRAGMENT;
import static personal.andrewthompson.task.task.Constants.PAST_TASK_LIFESPAN;
import static personal.andrewthompson.task.task.Constants.TASK_LIST_MONITOR_INTERVAL;
import static personal.andrewthompson.task.task.Constants.TASK_LIST_POSITION;
import static personal.andrewthompson.task.task.Constants.TASK_NAME;
import static personal.andrewthompson.task.task.Constants.TASK_NOTE;
import static personal.andrewthompson.task.task.Constants.WEEK_IN_HOURS;

/**
 * Created by Andy on 6/8/15.
 *
 * This class represents the list of tasks.
 * It handles adding of new tasks, completion of tasks, saving of completed or expired
 * tasks from the past week (so they can be quickly added back to the currentTasks),
 * and monitoring of the current tasks so that they are only present for 24 hours from
 * their date of creation.
 */
public class TaskList {
    private final FragmentManager fragManager;
    private ListView listView;
    private Vector<Task> weeklyTasks, currentTasks, pastTasks;
    private ArrayAdapter<Task> taskAdapter;
    private TextView header;
    private final Handler handler = new Handler();
    private final Runnable updateResults = new Runnable() {
        public void run() {
            updateHeader();
            colorDivider();
            taskAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Constructor for a TaskList.
     * @param context the context this Task list is in
     */
    public TaskList(Context context, ListView lView, TextView h, FragmentManager fm) {
        // set up current and past tasks
        currentTasks = new Vector<>();
        pastTasks = new Vector<>();
        weeklyTasks = new Vector<>();

        // un-comment this when testing to fill taskList with 10 tasks immediately
//        addTestTaskData(10);

        fragManager = fm;

        // set up adapter
        taskAdapter = new TaskAdapter(context, currentTasks, pastTasks);
        listView = lView;

        // set up the task list
        listView.setAdapter(taskAdapter);

        colorDivider();

        // set up header
        header = h;
        updateHeader();

        startTaskMonitoring();
    }

    /**
     * Set listView's divider color.
     */
    public void colorDivider() {
        int color = getUIColor();
        listView.setDivider(new ColorDrawable(color));
        listView.setDividerHeight(DIVIDER_HEIGHT);
    }

    /**
     * Monitor all task lists every TASK_LIST_MONITOR_INTERVAL millis.
     * All Tasks within a list that are older than the specified age are
     * removed from their list.
     */
    protected void startTaskMonitoring() {
        Thread monitorThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        // monitor the task lists
                        monitorTaskList(currentTasks, DAY_IN_HOURS);
                        monitorTaskList(weeklyTasks, WEEK_IN_HOURS);
                        monitorTaskList(pastTasks, PAST_TASK_LIFESPAN);

                        // update the header and UI color
                        handler.post(updateResults);

                        Thread.sleep(TASK_LIST_MONITOR_INTERVAL);
                    } catch (InterruptedException e) {}

                }
            }
        };
        monitorThread.start();
    }

    /**
     * Removes all tasks from taskList that were not created within the last n hours.
     * @param taskList the list of tasks to be checked
     * @param n the number of hours old any tasks in the list can be
     */
    private void monitorTaskList(Vector<Task> taskList, Long n) {
        // make list of all tasks older than the specified age (n hours)
        Vector<Task> tasksToRemove = new Vector<>();
        for (Task task: taskList) {
            if (!task.isWithinPastNHours(n)) {
                tasksToRemove.add(task);
            }
        }

        // remove old tasks from the taskList
        for (Task toRemove: tasksToRemove) {
            taskList.remove(toRemove);
        }
    }

    /**
     * Public method to allow other classes to add tasks to this TaskList's lists.
     */
    public void addToListAndUpdate(Task task) {
        currentTasks.add(task);
        weeklyTasks.add(task);
        updateList();
        updateHeader();
        colorDivider();
    }

    /**
     * Public method to allow activities containing a TaskList to call
     * notifyDataSetChanged() on their taskList object.
     */
    public void updateList() {
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * Builds and returns an array of strings holding all past task's names.
     * This is used to build the PastTaskList.
     * @return returns an array of strings holding all past task's names
     */
    public String[] getNamesArray() {
        int size = pastTasks.size();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = pastTasks.get(i).getName();
        }
        return names;
    }

    /**
     * Builds and returns an array of strings holding all past task's notes.
     * This is used to build the PastTaskList.
     * @return returns an array of strings holding all past task's notes
     */
    public String[] getNotesArray() {
        int size = pastTasks.size();
        String[] notes = new String[size];
        for (int i = 0; i < size; i++) {
            notes[i] = pastTasks.get(i).getNotes();
        }
        return notes;
    }

    /**
     * This method updates the header.
     */
    public void updateHeader() {
        String result = "Today " + getDailyCompleted() + "/" + getDailyCount()
                + "  |  Past week " + getWeeklyCompleted() + "/" + getWeeklyCount();
        header.setText(result);
    }

    /**
     * This method returns a double (corresponding to a color) that is representative
     * of how many tasks the user has completed in the past day and week.
     * @return returns a double corresponding to a color value
     */
    public double getUIColorWeight() {
        return 15.0 * ((double) getDailyCompleted() / (double) getDailyCount())
                + 5.0 * ((double) getWeeklyCompleted() / (double) getWeeklyCount());
    }

    /**
     * This method returns a color from the array of colors in Constants.java
     * @return a color from the array of colors in Constants.java
     */
    public int getUIColor() {
        return COLOR_LEVEL[(int) getUIColorWeight()];
    }

    /**
     * Private method that counts how many tasks have been completed in the past 24 hours.
     * @return an integer count of completed tasks in the past 24 hours.
     */
    private int getDailyCompleted() {
        int dailyComp = 0;
        for (Task t: weeklyTasks) {
            if (t.isWithinPastNHours(DAY_IN_HOURS) && t.isCompleted()) {
                dailyComp++;
            }
        }
        return dailyComp;
    }

    /**
     * Private method that counts how many tasks have been created in the past 24 hours.
     * @return an integer count of created tasks in the past 24 hours.
     */
    private int getDailyCount() {
        int dailyCount = 0;
        for (Task t: weeklyTasks) {
            if (t.isWithinPastNHours(DAY_IN_HOURS)) {
                dailyCount++;
            }
        }
        return dailyCount;
    }

    /**
     * Private method that counts how many tasks have been completed in the past week.
     * @return an integer count of completed tasks in the past week.
     */
    private int getWeeklyCompleted() {
        int weeklyComp = 0;
        for (Task t: weeklyTasks) {
            if (t.isCompleted()) {
                weeklyComp++;
            }
        }
        return weeklyComp;
    }

    private int getWeeklyCount() {
        return weeklyTasks.size();
    }

    /**
     * Custom inner class that subclasses ArrayAdapter for custom view of Tasks in the listView
     */
    private class TaskAdapter extends ArrayAdapter<Task> {
        private Vector<Task> daily, past;
        private float screenWidthDP, screenDensity, availableRoom;

        /**
         * Constructor for the TaskAdapter.
         * Also saves information about screen dimensions
         * @param c context
         * @param d daily tasks
         * @param p past tasks
         */
        public TaskAdapter(Context c, Vector<Task> d, Vector<Task> p) {
            super(c, 0, d);
            daily = d;
            past = p;
            screenDensity = c.getResources().getDisplayMetrics().density;
            screenWidthDP = pixelsToDP(c.getResources().getDisplayMetrics().widthPixels);
            availableRoom = screenWidthDP - 60;
        }

        /**
         * Removes a task from the list of daily tasks.
         * @param t the task to be removed
         */
        public void removeFromDaily(Task t) {
            super.remove(t);
            daily.remove(t);
            t.setCompleted(true);
            past.add(t);
        }

        @Override
        /**
         * Overrides getView.
         * Sets up the name and notes of a task, the button to complete each task
         * and allows for editing of task name and notes
         */
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            // Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.task_layout, parent, false);
            }
            final View finalView = view;

            // Get the data item for this position
            final Task currTask = getItem(position);

            // set up the textboxes (set their width accordingly)
            TextView taskName = (TextView) finalView.findViewById(R.id.taskName);
            TextView taskNotes = (TextView) finalView.findViewById(R.id.taskNotes);
            taskNotes.setText(currTask.getNotes());
            taskName.setText(currTask.getName());
            ViewGroup.LayoutParams params = taskName.getLayoutParams();
            params.width = (int) dpToPixels(availableRoom);
            taskName.setLayoutParams(params);

            // set up completed task button
            final ImageButton doneButton = (ImageButton) finalView.findViewById(R.id.doneButton);
            changeDoneButtonColor(doneButton, false);
            doneButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        changeDoneButtonColor(doneButton, true);
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        changeDoneButtonColor(doneButton, false);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        removeFromDaily(currTask);
                        notifyDataSetChanged();
                        updateHeader();
                        colorDivider();
                        SystemClock.sleep(150);
                    }
                    return false;
                }
            });

            // allow each task to be clicked on to bring up a fragment to edit the task
            // name and notes
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currTask.isCompleted()) {
                        Bundle b = new Bundle();
                        b.putString(TASK_NAME, currTask.getName());
                        b.putString(TASK_NOTE, currTask.getNotes());
                        b.putInt(TASK_LIST_POSITION, position);

                        EditTaskFragment frag = new EditTaskFragment();
                        frag.setArguments(b);
                        frag.show(fragManager, EDIT_TASK_FRAGMENT);
                    }
                }
            });

            // Return the completed view to render on screen
            return view;
        }

        /**
         * This method converts pixels to dp.
         * @param px the number of pixels to be converted
         * @return returns a pixel value in dp
         */
        private float pixelsToDP(float px) {
            return (px - .5f) / screenDensity;
        }

        /**
         * This method converts dp to pixels.
         * @param dp the number of dp to be converted
         * @return returns a dp value in pixels
         */
        private float dpToPixels(float dp) {
            return (dp * screenDensity) + .5f;
        }

        /**
         * This method changes the done button of a task to the appropriate color
         * and state (with or without a check mark).
         * If pressed is true, the done button will be represented by a circle with
         * a check mark inside. If false, the done button will be represented by
         * an empty circle. Buttons are also set to the appropriate color, determined
         * by a call to getUIColorWeight().
         * @param button the button to be set
         * @param pressed whether the button is currently pressed or not
         */
        private void changeDoneButtonColor(ImageButton button, boolean pressed) {
            // get the appropriate color of the button
            int color = (int) getUIColorWeight();

            // set the button to the correct color and state
            switch(color) {
                case 1:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_1);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_1);
                    }
                    break;
                case 2:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_2);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_2);
                    }
                    break;
                case 3:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_3);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_3);
                    }
                    break;
                case 4:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_4);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_4);
                    }
                    break;
                case 5:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_5);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_5);
                    }
                    break;
                case 6:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_6);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_6);
                    }
                    break;
                case 7:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_7);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_7);
                    }
                    break;
                case 8:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_8);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_8);
                    }
                    break;
                case 9:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_9);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_9);
                    }
                    break;
                case 10:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_10);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_10);
                    }
                    break;
                case 11:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_11);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_11);
                    }
                    break;
                case 12:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_12);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_12);
                    }
                    break;
                case 13:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_13);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_13);
                    }
                    break;
                case 14:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_14);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_14);
                    }
                    break;
                case 15:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_15);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_15);
                    }
                    break;
                case 16:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_16);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_16);
                    }
                    break;
                case 17:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_17);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_17);
                    }
                    break;
                case 18:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_18);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_18);
                    }
                    break;
                case 19:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_19);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_19);
                    }
                    break;
                case 20:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_20);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_20);
                    }
                    break;
                default:
                    if (pressed) {
                        button.setImageResource(R.drawable.done_pressed_color_level_0);
                    } else {
                        button.setImageResource(R.drawable.done_color_level_0);
                    }
                    break;
            }
        }
    }

    /**
     * Private method to add n Tasks to a tasklist.
     * This method is used for testing purposes to artificially fill a task list
     * for testing purposes.
     * @param nTasks the number of fake tasks to create
     */
    private void addTestTaskData(int nTasks) {
        for (int i = 0; i < nTasks; i++) {
            Task t = new Task("Fake task p" + i, "pnotes for task " + i);
            currentTasks.add(t);
            weeklyTasks.add(t);
        }
    }

    public Vector<Task> getCurrentTasks() {
        return currentTasks;
    }

    public Vector<Task> getPastTasks() {
        return pastTasks;
    }
}
