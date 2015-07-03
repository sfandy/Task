package personal.andrewthompson.task.task;

/**
 * Created by Andrew Thompson on 6/10/15.
 */
public interface OnFragmentCompleteListener {
    /**
     * This method is used as a callback when an AddTaskTabFragment is opened, in order to
     * pass data back to the calling Activity.
     * @param name The name of the task
     * @param notes The notes of the task
     * @param position The task to be removed from previous task list
     */
    public abstract void onNewTaskComplete(String name, String notes, int position);

    /**
     * This method is used as a callback when an EditTaskFragment is opened, in order to
     * pass data back to the calling Activity.
     * @param name The updated name of the task
     * @param notes The updated notes of the task
     * @param position The position of the task to be modified.
     */
    public abstract void onEditTaskComplete(String name, String notes, int position);
}