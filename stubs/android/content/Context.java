package android.content;

public interface Context {
    public Object getApplicationContext();

    public int checkSelfPermission(String permission);
}
