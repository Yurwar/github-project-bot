package edu.kpi.dto;

import edu.kpi.model.Sentiment;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TweetData {

    long id;
    Date creationTime;
    String originalText;
    String text;
    String userName;
    String screenName;
    String profileImageUrl;
    int sentiment;

    public TweetData(long id,
                     Date creationTime,
                     String originalText,
                     String text,
                     String userName,
                     String screenName,
                     String profileImageUrl) {

        this.id = id;
        this.creationTime = creationTime;
        this.originalText = originalText;
        this.text = text;
        this.userName = userName;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.sentiment = Sentiment.NEUTRAL.getValue();
    }

}
