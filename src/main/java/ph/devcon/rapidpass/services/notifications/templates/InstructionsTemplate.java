package ph.devcon.rapidpass.services.notifications.templates;

public class InstructionsTemplate implements NotificationTemplate<String> {
    @Override
    public String compose() {
        return "See instructions at https://docs.google.com/document/d/1GVEJ-86-KDKY11ScM8HMV5dltTP9VpEWNTwn6SJe258/edit";
    }
}
