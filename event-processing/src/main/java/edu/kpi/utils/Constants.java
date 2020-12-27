package edu.kpi.utils;

public final class Constants {
    public static final class Telegram {
        public static final String TAGS_COMMAND_NAME = "tags";
        public static final String TAGS_COMMAND_DESCRIPTION = "Put tags to follow updates of your project in twitter";
        public static final String START_COMMAND_NAME = "start";
        public static final String START_COMMAND_DESCRIPTION = "Start command";
        public static final String START_COMMAND_MESSAGE = "Welcome to the Github Project Bot,\n" +
                "To start use bot please fill info" +
                " about which project you want to track using /project github_project_url\n" +
                "After that you can use /tags tag1 tag2 command to subscribe on daily twitter updates relating your project";
        public static final String PROJECT_COMMAND_NAME = "project";
        public static final String PROJECT_COMMAND_DESCRIPTION = "Put github project url to track your project";
        public static final String UNRECOGNIZED_COMMAND_MESSAGE = "Unrecognized command";
        public static final String CHAT_ID_COMMAND_NAME = "chatid";
        public static final String CHAT_ID_COMMAND_DESCRIPTION = "Use this command to discover your chat id";
        public static final String CHAT_ID_COMMAND_MESSAGE = "Your chat id: ";
    }

    public static final String OPENED = "opened";
    public static final String CLOSED = "closed";
    // TODO check spelling
    public static final String COMMENTED = "commented";
}
