package org.bahmni.module.bahmni.ie.apps.config;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.ParserImpl;

import java.util.ArrayList;
import java.util.List;

public class PdfFormConfig {

    List<String> styles = new ArrayList();
    private Logger logger = Logger.getLogger(ParserImpl.class);


    public void setMaximumWidth(int value) {
        String valueInPercentage = value + "%";

        styles.add("max-width:" + valueInPercentage);
    }

    public void setWidth(int value) {
        String valueInPercentage = value + "%";

        styles.add("width:" + valueInPercentage);
    }

    public void setContentFieldBox(int widthInPixels, String style, String color) {
        String widthInPercentage = widthInPixels + "px";

        styles.add("border-bottom:" + widthInPercentage + " " + style + " " + color);
    }

    public String getStyles() {
        String style = "style= \"";
        for (String styleValue : styles) {
            style += styleValue;
            style += ";";
        }
        style += "\"";
        styles.clear();
        return style;
    }
}
