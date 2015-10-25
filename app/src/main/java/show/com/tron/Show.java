package show.com.tron;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Show {

    private String name;
    private Day weekDay;
    private int noOfEpisodes;
    private int season;
    private int episode;
    private int id;//primary key in database
    private Date lastUpdated;
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public Show(String name, Day weekday, int noOfEpisodes, int season, int episode, Date lastUpdated) {
        this.name = name;
        this.weekDay = weekday;
        this.noOfEpisodes = noOfEpisodes;
        this.season = season;
        this.episode = episode;
        this.lastUpdated = lastUpdated;
    }

    public Show(String name, String weekday, int noOfEpisodes, int season, int episode, int id, Long lastUpdated) {
        this.name = name;
        this.weekDay = Day.valueOf(weekday);
        this.noOfEpisodes = noOfEpisodes;
        this.season = season;
        this.episode = episode;
        this.id = id;
        this.lastUpdated = new Date(lastUpdated);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Day getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Day weekDay) {
        this.weekDay = weekDay;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoOfEpisodes() {
        return noOfEpisodes;
    }

    public void setNoOfEpisodes(int noOfEpisodes) {
        this.noOfEpisodes = noOfEpisodes;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    /**
     * Used to get the current season and episode as a string.
     *
     * @return String concatenation of current season and episode
     */
    public String getSeasonEpisode() {
        return "S" + this.season + "E" + this.episode;
    }

    /**
     * Used to increment the current episode.
     *
     * @return String concatenation of new season and episode
     */
    public String nextEpisode() {
        if (episode == noOfEpisodes) {
            episode = 1;
            season = season + 1;
        } else {
            episode += 1;
        }
        return getSeasonEpisode();
    }

    /**
     * Used to decrement the current episode.
     *
     * @return String concatenation of new season and episode
     */
    public String prevEpisode() {
        if (episode <= 1) {
            episode = noOfEpisodes;
            season = season - 1;
        } else {
            episode -= 1;
        }
        return getSeasonEpisode();
    }

    public String getDialogShowSeason() {
        return "Season " + season + " Episode " + episode + ".";
    }

    public String getDialogNoEpisodes() {
        return noOfEpisodes + " episodes per season.";
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdatedMillis( ) {
        return lastUpdated.getTime();
    }

    public String getLastUpdated() {
        long diff = System.currentTimeMillis() - lastUpdated.getTime();
        long last = 0;
        if ( lastUpdated.getTime() <= 0 ) {
            return "";
        } else if ( 59 >= (last = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)) ) {
            if ( last != 1 ) {
                return last + " minutes ago";
            } else {
                return last + " second ago";
            }
        } else if ( 23 >= (last = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)) ) {
            if ( last != 1 ) {
                return last + " hours ago";
            } else {
                return last + " hour ago";
            }
        } else if ( 31 >= (last = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)) ) {
            if (last != 1) {
                return last + " days ago";
            } else {
                return last + " day ago";
            }
        } else {
            return formatter.format(lastUpdated);
        }
    }
}

