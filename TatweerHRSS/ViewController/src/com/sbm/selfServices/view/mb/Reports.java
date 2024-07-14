package com.sbm.selfServices.view.mb;

import oracle.adf.view.rich.component.rich.output.RichInlineFrame;

public class Reports {
    private RichInlineFrame iframComp;
    private String url =
        "https://dbtatweert1-noor3ttar.db.em2.oraclecloudapps.com/apex/f?p=30400137:1:23853022748023:::::";
    public Reports() {
    }

    public void setIframComp(RichInlineFrame iframComp) {
        this.iframComp = iframComp;
    }

    public RichInlineFrame getIframComp() {
        return iframComp;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
