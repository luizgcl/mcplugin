package br.com.luizgcl.utils;

public class Util {
  public static String time(int totalSeconds) {
    int minutes = totalSeconds/60,
        seconds = totalSeconds%60;
    return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
  }

  public static String formatMillis(long millis) {
    return (System.currentTimeMillis() - millis) + "ms";
  }
}
