package personal.andrewthompson.task.task;

/**
 * Created by Andy on 6/10/15.
 *
 * This interface is used to pass data from a fragment to an Activity.
 * The activity must implement this interface, overriding the two methods as needed.
 * Fragments must also have a reference to an OnFragmentCompleteListener, which is
 * set in by overriding the onAttach method. When a fragment wants to send information
 * back to the activity, it calls one of the below methods on its OnFragmentCompleteListener
 * reference, passing the necessary information through the method call.
 */
public interface OnFragmentCompleteListener {
    /**
     * This method is used as a callback when an AddTaskTabFragment is opened, in order to
     * pass data back to the calling Activity.
     * @param name The name of the task
     * @param notes The notes of the task
     * @param position The task to be removed from previous task list
     */
    void onNewTaskComplete(String name, String notes, int position);

    /**
     * This method is used as a callback when an EditTaskFragment is opened, in order to
     * pass data back to the calling Activity.
     * @param name The updated name of the task
     * @param notes The updated notes of the task
     * @param position The position of the task to be modified.
     */
    void onEditTaskComplete(String name, String notes, int position);
}