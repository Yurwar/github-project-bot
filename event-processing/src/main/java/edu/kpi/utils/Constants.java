package edu.kpi.utils;

public final class Constants {

    public static final class Telegram {

        public static final String TAGS_COMMAND_NAME = "tags";
        public static final String TAGS_COMMAND_DESCRIPTION = "Put tags to follow updates of your project in twitter";
        public static final String START_COMMAND_NAME = "start";
        public static final String START_COMMAND_DESCRIPTION = "Start command";
        public static final String START_COMMAND_MESSAGE = "Welcome to the Github Project Bot,\n" +
                "To start use bot please get your chat id using /chatid command and fill it to your server\n" +
                "After that you can use /tags tag1 tag2 command to subscribe on daily twitter updates relating your project";
        public static final String UNRECOGNIZED_COMMAND_MESSAGE = "Unrecognized command";
        public static final String CHAT_ID_COMMAND_NAME = "chatid";
        public static final String CHAT_ID_COMMAND_DESCRIPTION = "Use this command to discover your chat id";
        public static final String CHAT_ID_COMMAND_MESSAGE = "Your chat id: ";
        public static final String TAGS_COMMAND_MESSAGE = "You choose next tags to follow: ";

        public static final class Message {

            public final static String BLUE_CIRCLE = "\uD83D\uDD35";
            public final static String GREEN_CIRCLE = "\uD83D\uDFE2";
            public final static String YELLOW_CIRCLE = "\uD83D\uDFE1";
            public final static String BOT_ICON = "\uD83E\uDD16";
            public final static String CONVERSATION_BALLOON = "\uD83D\uDCAC";
            public final static String TWISTED_ARROWS = "\uD83D\uDD00";
            public final static String CHECK_MARK = "\u2705";
            public final static String CROSS_MARK = "\u274C";
            public final static String RIGHT_ARROW = "\u27A1\uFE0F";
            public static final String TAG_SYMBOL = "\uD83D\uDD30";
            public static final String CHART_SYMBOL = "\uD83D\uDCCA";
            public static final String DIAMOND_SYMBOL = "\uD83D\uDD38";
            public static final String SPEAKING_HEAD = "\uD83D\uDDE3";
            public static final String RAGE_FACE = "\uD83E\uDD2C";
            public static final String ANGRY_FACE = "\uD83D\uDE20";
            public static final String NEUTRAL_FACE = "\uD83D\uDE10";
            public static final String RELAXED_FACE = "\uD83D\uDE0A";
            public static final String STAR = "\u2B50";
        }
    }

    public static final class GitHub {

        public static final String BOT_TYPE = "Bot";
        public static final String USER_TYPE = "User";
    }

    public static final String OPENED = "opened";
    public static final String CLOSED = "closed";
    public static final String LABELED = "labeled";
    public static final String MERGED = "merged";
    public static final String DECLINED = "declined";
    // TODO check spelling
    public static final String COMMENTED = "commented";
}
