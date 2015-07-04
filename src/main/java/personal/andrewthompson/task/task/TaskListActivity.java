package personal.andrewthompson.task.task;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import static personal.andrewthompson.task.task.Constants.ADD_TASK_FRAGMENT;
import static personal.andrewthompson.task.task.Constants.LIST_COLOR;
import static personal.andrewthompson.task.task.Constants.TASK_NAMES;
import static personal.andrewthompson.task.task.Constants.TASK_NOTES;

/**
 * Created by Andy on 6/10/15.
 *
 * This class is the main activity of Task.
 * It contains a taskList, has a header displaying your degree of success in completing tasks
 * in the past 24 hours and week, has a button to add new tasks and allows for the user to
 * mark tasks as completed.
 */
public class TaskListActivity extends ActionBarActivity implements OnFragmentCompleteListener {
    private TaskList taskList;
    private FragmentManager fragManager;
    private boolean dragging = false;
    private final Handler dragHandler = new Handler();
    Runnable addLongPressed = new Runnable() {
        public void run() {
            dragging = true;
        }
    };

    @Override
    /**
     * Overriding the onCreate method.
     * The task list is set up, the initial header is created and the add new
     * task button is placed in an appropriate position on the screen (while
     * also allowing the user to drag the button to a more convenient location
     * to accomodate lefties, etc.).
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fragManager = getSupportFragmentManager();
        TextView header = (TextView) findViewById(R.id.header);
        ListView lview = (ListView) findViewById(R.id.taskListView);
        taskList = new TaskList(this, lview, header, fragManager);

        // set position of add task button
        final ImageButton addButton = (ImageButton) findViewById(R.id.addTaskButton);
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        addButton.setX(screenWidth - (screenWidth / 3));
        addButton.setY(screenHeight - (screenHeight / 4));

        // set up dragging and clicking of add button
        addButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                float originalX = addButton.getX() - addButton.getWidth() / 2;
                float originalY = addButton.getY() - addButton.getHeight() / 2;

                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    dragHandler.postDelayed(addLongPressed, 700);
                    addButton.setBackgroundResource(R.drawable.add_button_pressed);
                    return true;
                } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (dragging) {
                        float newX = originalX + e.getX();
                        float newY = originalY + e.getY();
                        if ((newY > 10) && (newY < screenHeight - 30) && (newX > -10) && (newX < screenWidth - 30)) {
                            addButton.setX(originalX + e.getX());
                            addButton.setY(originalY + e.getY());
                        }
                    }
                    addButton.setBackgroundResource(R.drawable.add_button);
                    return true;
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (!dragging) {
                        dragHandler.removeCallbacks(addLongPressed);
                        addButton.setBackgroundResource(R.drawable.add_button);

                        // create a fragment for adding tasks (new or old)
                        Bundle bdl = new Bundle(3);
                        bdl.putStringArray(TASK_NAMES, taskList.getNamesArray());
                        bdl.putStringArray(TASK_NOTES, taskList.getNotesArray());
                        bdl.putInt(LIST_COLOR, taskList.getUIColor());
                        AddTaskTabFragment frag = new AddTaskTabFragment();
                        frag.setArguments(bdl);
                        frag.show(fragManager, ADD_TASK_FRAGMENT);
                    }
                    dragging = false;
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * This method receives data from an AddTaskTabFragment.
     */
    public void onNewTaskComplete(String name, String notes, int position) {
        // tasks are only added if they contain a valid name
        if (name.length() > 0) {
            Task task = new Task(name, notes);
            taskList.addToListAndUpdate(task);
            if (position > -1) {
                taskList.getPastTasks().remove(taskList.getPastTasks().size() - (position + 1));
            }
        }
    }

    @Override
    /**
     * This method receives data from an EditTaskFragment.
     */
    public void onEditTaskComplete(String name, String notes, int position) {
        Task task = taskList.getCurrentTasks().get(position);

        task.setName(name);
        task.setNotes(notes);
        taskList.updateList();
    }
}
