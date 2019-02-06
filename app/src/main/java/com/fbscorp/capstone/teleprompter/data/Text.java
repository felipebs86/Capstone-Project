package com.fbscorp.capstone.teleprompter.data;

public class Text {
    public String title;
    public String desc;
    public Long id;

    public Text() {}

    public Text(String title, String desc, Long id) {
        this.title = title;
        this.desc = desc;
        this.id = id;
    }
}
