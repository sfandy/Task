package personal.andrewthompson.task.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static personal.andrewthompson.task.task.Constants.ADD_TASK_FRAGMENT;
import static personal.andrewthompson.task.task.Constants.LIST_COLOR;
import static personal.andrewthompson.task.task.Constants.TASK_NAMES;
import static personal.andrewthompson.task.task.Constants.TASK_NOTES;

/**
 * Created by Andrew Thompson on 6/10/15.
 */
public class AddPastTaskFragment extends ListFragment {
    private OnFragmentCompleteListener completeListener;
    private ArrayList<Task> pastList;
    private String[] names, notes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        pastList = new ArrayList<Task>();

        names = getArguments().getStringArray(TASK_NAMES);
        notes = getArguments().getStringArray(TASK_NOTES);

        TaskAdapter adapter = new TaskAdapter(getActivity(), pastList);
        setListAdapter(adapter);

        int color = getArguments().getInt(LIST_COLOR);
        ListView listView = (ListView) getView().findViewById(android.R.id.list);
        listView.setDivider(new ColorDrawable(color));
        listView.setDividerHeight(1);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView textView = (TextView) getView().findViewById(R.id.noPastTasksText);
        if (names.length > 0) {
            textView.setVisibility(View.INVISIBLE);
            // add past tasks in order of newest first
            for (int i = names.length-1; i > -1; i--) {
                pastList.add(new Task(names[i], notes[i]));
            }
        } else {
            textView.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Task task = pastList.get(position);
        String name = task.getName();
        String notes = task.getNotes();
        completeListener.onNewTaskComplete(name, notes, position);

        getActivity().getSupportFragmentManager().beginTransaction().remove(
                getActivity().getSupportFragmentManager().findFragmentByTag(ADD_TASK_FRAGMENT)).commit();
    }

    /**
     * Custom inner class that subclasses ArrayAdapter for custom view of Tasks in the listView
     */
    private class TaskAdapter extends ArrayAdapter<Task> {
        private ArrayList<Task> past;

        public TaskAdapter(Context context, ArrayList<Task> p) {
            super(context, 0, p);
            past = p;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            // Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.past_task_layout, parent, false);
            }

            // Get the data item for this position
            final Task currTask = getItem(position);

            // set name and notes text for task
            TextView nameView = (TextView) view.findViewById(R.id.pastTaskName);
            nameView.setText(currTask.getName());
            TextView notesView = (TextView) view.findViewById(R.id.pastTaskNotes);
            notesView.setText(currTask.getNotes());

            // Return the completed view to render on screen
            return view;
        }
    }

}