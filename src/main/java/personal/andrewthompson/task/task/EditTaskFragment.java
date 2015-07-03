package personal.andrewthompson.task.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import static personal.andrewthompson.task.task.Constants.TASK_LIST_POSITION;
import static personal.andrewthompson.task.task.Constants.TASK_NAME;
import static personal.andrewthompson.task.task.Constants.TASK_NOTE;

/**
 * Created by Andy on 6/11/15.
 *
 * This class represents a fragment used to edit a task's name or notes.
 */
public class EditTaskFragment extends DialogFragment {
    private OnFragmentCompleteListener completeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_task_layout, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // set text of name editor
        EditText nameEditor = (EditText) view.findViewById(R.id.editTaskNameTextbox);
        nameEditor.setText(getArguments().getString(TASK_NAME));

        // set text of notes editor
        EditText notesEditor = (EditText) view.findViewById(R.id.editTaskNotesTextbox);
        notesEditor.setText(getArguments().getString(TASK_NOTE));

        // set up the done button to pass the new name and notes back to the TaskListActivity
        Button button = (Button) view.findViewById(R.id.submitNewTaskButton);
        button.setText("Done");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add the new task to the list
                EditText nameText = (EditText) view.findViewById(R.id.editTaskNameTextbox);
                EditText notesText = (EditText) view.findViewById(R.id.editTaskNotesTextbox);
                String name = nameText.getText().toString();
                String notes = notesText.getText().toString();
                completeListener.onEditTaskComplete(name, notes, getArguments().getInt(TASK_LIST_POSITION));

                dismiss();
            }
        });

        return view;
    }

    /**
     * This method allows the fragment to send information back to the TaskListActivity.
     * @param activity The activity to send information to
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            completeListener = (OnFragmentCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentCompleteListener");
        }
    }
}
