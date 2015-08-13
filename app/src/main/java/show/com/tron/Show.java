package show.com.tron;

public class Show {

    private String name;
    private Day weekDay;
    private int noOfEpisodes;
    private int season;
    private int episode;
    private int id;//primary key in database

    public Show(String name, Day weekday, int noOfEpisodes, int season, int episode) {
        this.name = name;
        this.weekDay = weekday;
        this.noOfEpisodes = noOfEpisodes;
        this.season = season;
        this.episode = episode;
    }

    public Show(String name, String weekday, int noOfEpisodes, int season, int episode, int id) {
        this.id = id;
        this.name = name;
        this.weekDay = Day.valueOf(weekday);
        this.noOfEpisodes = noOfEpisodes;
        this.season = season;
        this.episode = episode;
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
}

