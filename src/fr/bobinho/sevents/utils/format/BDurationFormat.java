package fr.bobinho.sevents.utils.format;

public class BDurationFormat {

    /**
     * Gets a duration string in minutes - seconds format
     *
     * @param durationInSecond the duration in second to format
     * @return the formatted string in minutes - seconds format
     */
    public static String getAsMinuteSecondFormat(long durationInSecond) {
        return ((int) durationInSecond / 60) + ":" + (durationInSecond % 60 < 10 ? "0" : "") + (durationInSecond % 60);
    }

    /**
     * Gets a duration string in seconds format
     *
     * @param durationInSecond the duration in second to format
     * @return the formatted string in seconds format
     */
    public static String getAsSecondFormat(long durationInSecond) {
        return durationInSecond + "s ";
    }

}
