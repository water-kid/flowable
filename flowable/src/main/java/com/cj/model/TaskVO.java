package com.cj.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TaskVO  {
    private Integer days;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date startTime;
    // 流程的审批人
    private String approveUser;

    // task Id
    private String taskId;
    // task是否通过
    private Boolean approval;


    private String applicant;

    public TaskVO(Integer days, String reason, Date endTime, Date startTime, String approveUser, String taskId, Boolean approval, String applicant) {
        this.days = days;
        this.reason = reason;
        this.endTime = endTime;
        this.startTime = startTime;
        this.approveUser = approveUser;
        this.taskId = taskId;
        this.approval = approval;
        this.applicant = applicant;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public TaskVO() {
    }


    public TaskVO(Integer days, String reason, Date endTime, Date startTime, String approveUser, String taskId, Boolean approval) {
        this.days = days;
        this.reason = reason;
        this.endTime = endTime;
        this.startTime = startTime;
        this.approveUser = approveUser;
        this.taskId = taskId;
        this.approval = approval;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getApproval() {
        return approval;
    }

    public void setApproval(Boolean approval) {
        this.approval = approval;
    }
}
