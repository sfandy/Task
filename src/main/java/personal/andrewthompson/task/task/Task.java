package personal.andrewthompson.task.task;

import org.joda.time.DateTime;

/**
 * Created by Andy on 6/8/15.
 *
 * This class represents a task.
 * It contains information such as the task's name, notes, whether it is
 * completed or not and the time it was created. I chose to use a Joda
 * DateTime as it is recommended over the Java 7 Calendar object (which was
 * luckily replaced in Java 8, which unfortunately is not available when
 * developing for Android). In addition, the Joda DateTime gives me more
 * experience working with external libraries and working with Gradle.
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

    /**
     * This method calculates this Task's age in milliseconds.
     * @return this task's age in milliseconds
     */
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
