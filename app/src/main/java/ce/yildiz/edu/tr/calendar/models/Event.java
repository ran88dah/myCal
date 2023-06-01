package ce.yildiz.edu.tr.calendar.models;

public class Event {

    private int id;
    private String title;
    private boolean isAllDay;
    private String date;
    private String time;
    private String month;
    private String year;
    private boolean isNotify;
    private boolean isRecurring;
    private String recurringPeriod;
    private String note;
    private int color;

    private String priority; //toDB
    private String type; //toDB
    private int parentId;

    public Event() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    ///added by sarah
    public String getPriority() { return priority; }
    public String getType() {
        return type;
    }

    public void setPriority(String p){ priority=p; }

    public void setType(String t){ type=t; }
    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }




    @Override
    public String toString() {
        return title + '\n' +
                "DATE=" + date + '\n' +
                "TIME=" + time + '\n' +
                "PRIORITY=" + priority + '\n' +
                "NOTE=" + note + '\n' +
                "TYPE=" + type + '\n';
    }


}
