package com.xz.shangde;

/**
 * @author zxz
 * 存储TASK信息
 */

public class Task{
    private int ID;
    private int creator;
    private int farmid;
    private int examiner;
    private int type;
    private String description;
    private int state;
    private boolean agree;
    private String createdate;
    private String processdate;

    public Task(int ID, int creator, int farmid, int examiner, int type, String description,int state, boolean
            agree, String createdate, String processdate) {
        this.ID = ID;
        this.creator = creator;
        this.farmid = farmid;
        this.examiner = examiner;
        this.type = type;
        this.description=description;
        this.state = state;
        this.agree = agree;
        this.createdate = createdate;
        this.processdate = processdate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getFarmid() {
        return farmid;
    }

    public void setFarmid(int farmid) {
        this.farmid = farmid;
    }

    public int getExaminer() {
        return examiner;
    }

    public void setExaminer(int examiner) {
        this.examiner = examiner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAgree() {
        return agree;
    }

    public void setAgree(boolean agree) {
        this.agree = agree;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getProcessdate() {
        return processdate;
    }

    public void setProcessdate(String processdate) {
        this.processdate = processdate;
    }
}
