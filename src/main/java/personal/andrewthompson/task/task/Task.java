package personal.andrewthompson.task.task;

import org.joda.time.DateTime;

/**
 * Created by Andrew Thompson on 6/8/15.
 */
public class Task {
    private String name, notes;
    private boolean completed;
    private DateTime timeAdded;

    public Task(String na, String no) {
        name = na;
        notes = no;
        completed = false;
        timeAdded = new DateTime();
    }

    /**
     * This method checks if this task was added within the last n hours.
     * @return returns true if this task is younger than n hours old, false if not
     */
    public boolean isWithinPastNHours(Long n) {
        Long diff = ageInMillis();
        diff = diff / 1000L / 60L / n;               // get the difference in hours
        return diff.compareTo(n) < 0;
    }

    private Long ageInMillis() {
        Long millisAdded = timeAdded.getMillis();
        Long millisCurr = System.currentTimeMillis();
        return millisCurr - millisAdded;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public DateTime getTimeAdded() {
        return timeAdded;
    }

    public void setNotes(String no) {
        notes = no;
    }

    public void setName(String na) {
        name = na;
    }

    public void setCompleted(boolean comp) {
        completed = comp;
    }
}
