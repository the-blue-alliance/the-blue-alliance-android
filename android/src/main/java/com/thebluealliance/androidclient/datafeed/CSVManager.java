package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.models.SimpleTeam;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Nathan on 5/2/2014.
 */
public class CSVManager {

    public static ArrayList<SimpleTeam> parseTeamsFromCSV(String CSV) {
        ArrayList<SimpleTeam> teams = new ArrayList<>();

        Reader reader = new StringReader(CSV);
        try {
            List<String> values = CSVHelper.parseLine(reader);
            int i = 1;
            while (values != null) {
                StringBuilder sb = new StringBuilder();
                if (values.size() < 4 || values.get(1).toLowerCase().equals("none")) {
                    values = CSVHelper.parseLine(reader);
                    continue;
                }
                String teamKey = "frc" + values.get(0);
                try {
                    String teamName = values.get(2);
                    if (teamName.isEmpty()) {
                        teamName = "Team " + Integer.parseInt(values.get(0));
                    }
                    SimpleTeam team = new SimpleTeam(teamKey, Integer.parseInt(values.get(0)), teamName, values.get(3), -1);
                    teams.add(team);
                } catch (NumberFormatException e) {
                    // Invalid team number. Probably the column header.
                }
                values = CSVHelper.parseLine(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return teams;
    }

    public static class CSVHelper {
        public static void writeLine(Writer w, List<String> values)
                throws Exception {
            boolean firstVal = true;
            for (String val : values) {
                if (!firstVal) {
                    w.write(",");
                }
                w.write("\"");
                for (int i = 0; i < val.length(); i++) {
                    char ch = val.charAt(i);
                    if (ch == '\"') {
                        w.write("\"");  //extra quote
                    }
                    w.write(ch);
                }
                w.write("\"");
                firstVal = false;
            }
            w.write("\n");
        }

        /**
         * Returns a null when the input stream is empty
         */
        public static List parseLine(Reader r) throws Exception {
            int ch = r.read();
            while (ch == '\r') {
                ch = r.read();
            }
            if (ch < 0) {
                return null;
            }
            Vector store = new Vector();
            StringBuffer curVal = new StringBuffer();
            boolean inquotes = false;
            boolean started = false;
            while (ch >= 0) {
                if (inquotes) {
                    started = true;
                    if (ch == '\"') {
                        inquotes = false;
                    } else {
                        curVal.append((char) ch);
                    }
                } else {
                    if (ch == '\"') {
                        inquotes = true;
                        if (started) {
                            // if this is the second quote in a value, add a quote
                            // this is for the double quote in the middle of a value
                            curVal.append('\"');
                        }
                    } else if (ch == ',') {
                        store.add(curVal.toString());
                        curVal = new StringBuffer();
                        started = false;
                    } else if (ch == '\r') {
                        //ignore LF characters
                    } else if (ch == '\n') {
                        //end of a line, break out
                        break;
                    } else {
                        curVal.append((char) ch);
                    }
                }
                ch = r.read();
            }
            store.add(curVal.toString());
            return store;
        }
    }
}
