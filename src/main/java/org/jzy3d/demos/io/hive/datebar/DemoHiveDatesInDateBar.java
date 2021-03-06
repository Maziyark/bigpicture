package org.jzy3d.demos.io.hive.datebar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.jzy3d.chart.BigPicture;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.Type;
import org.jzy3d.colors.Color;
import org.jzy3d.demos.drawing.datebar.HistogramDate;
import org.jzy3d.demos.drawing.datebar.HistogramDate2d;
import org.jzy3d.io.Config;
import org.jzy3d.io.hive.jdbc.HiveJdbcClient;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class DemoHiveDatesInDateBar {
    
    static Config.HiveConnection connectionSettings = new Config.HiveConnection("172.16.255.136", "default", "root", "hadoop");

    static String table = "employee_logins";
    
    public static Type dims = Type.dd;

    public static void main(String[] args) throws SQLException {
        List<DateTime> events = getDates(connectionSettings, table);
        System.out.println("read " + events.size() + " events");

        plot(events);
    }

    private static void plot(List<DateTime> events) {
        // Model
        TicToc.T.tic();
        HistogramDate hist = new HistogramDate(events);
        System.out.println(hist.getRanges().length + " bins");
        TicToc.T.tocShow("gen hist");

        // Drawable
        TicToc.T.tic();
        HistogramDate2d histogram = new HistogramDate2d(hist, Color.CYAN, Color.GRAY);
        TicToc.T.tocShow("made drawable");

        // Chart
        Chart chart = BigPicture.chart(histogram.getDrawable(), dims, Quality.Nicest);
        histogram.layout(chart);
    }

    public static List<DateTime> getDates(Config.HiveConnection connectionSettings, String table) throws SQLException {
        HiveJdbcClient hive = new HiveJdbcClient();
        Statement stmt = hive.connect(connectionSettings).createStatement();

        List<DateTime> dates = new ArrayList<DateTime>();
        ResultSet rs = hive.selectAll(stmt, table);
        while (rs.next()) {
            String value = rs.getString(1);
            dates.add(new DateTime(value));
        }
        return dates;
    }

}
