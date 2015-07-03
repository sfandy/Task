package personal.andrewthompson.task.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static personal.andrewthompson.task.task.Constants.ADD_TASK_FRAGMENT;

/**
 * Created by Andy on 6/10/15.
 */
public class AddNewTaskFragment extends DialogFragment {
    private OnFragmentCompleteListener completeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_task_layout, container, false);

        // override Android bug where EditTexts in TabHosts lose focus when clicked
        final EditText nameText = (EditText) view.findViewById(R.id.newTaskNameText);
        nameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                nameText.requestFocusFromTouch();
                return false;
            }
        });
        nameText.requestFocus();

        // override Android bug where EditTexts in TabHosts lose focus when clicked
        final EditText notesText = (EditText) view.findViewById(R.id.newTaskNotesText);
        notesText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                notesText.requestFocusFromTouch();
                return false;
            }
        });

        // add submit button
        Button addTaskButton = (Button) view.findViewById(R.id.addNewTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add the new task to the list
                EditText nameText = (EditText) view.findViewById(R.id.newTaskNameText);
                EditText notesText = (EditText) view.findViewById(R.id.newTaskNotesText);
                String name = nameText.getText().toString();
                String notes = notesText.getText().toString();
                completeListener.onNewTaskComplete(name, notes, -1);

                getActivity().getSupportFragmentManager().beginTransaction().remove(
                        getActivity().getSupportFragmentManager().findFragmentByTag(ADD_TASK_FRAGMENT)).commit();
            }
        });

        return view;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            completeListener = (OnFragmentCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentCompleteListener");
        }
    }
}